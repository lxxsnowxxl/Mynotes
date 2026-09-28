package com.example.mynotes.ui

import androidx.compose.runtime.Immutable

@Immutable
internal data class DevelopmentFileDoc(
    val category: String, val title: String, val path: String, val purpose: String, val whereInApp: String,
    val details: String, val keySymbols: List<String>, val internalDependencies: List<String>, val lineCount: Int, val assetName: String
)

internal object DevelopmentCodeCatalog {
    const val kotlinFileCount = 87
    const val resourceFileCount = 682
    val entries: List<DevelopmentFileDoc> = listOf(
        DevelopmentFileDoc(
            category = "Núcleo y actividad", title = "AppActivitySupport.kt",
            path = "app/src/main/java/com/example/mynotes/AppActivitySupport.kt",
            purpose = "Centraliza políticas que varias Activity comparten: idioma guardado, modo inmersivo de barras del sistema, apariencia claro/oscuro, sincronización del perfil de rendimiento y sincronización del feedback sonoro/háptico.",
            whereInApp = "Se ejecuta alrededor de MainActivity, AttachmentViewerActivity y actividades de PDF; su efecto se nota al cambiar idioma, entrar/salir de pantalla completa, modificar el perfil de rendimiento o cambiar sonidos/hápticos.",
            details = "Centraliza políticas que varias Activity comparten: idioma guardado, modo inmersivo de barras del sistema, apariencia claro/oscuro, sincronización del " +
                "perfil de rendimiento y sincronización del feedback sonoro/háptico. En ejecución, este archivo se sitúa por encima de las pantallas: prepara el " +
                "contexto/ventana o coordina destinos antes de delegar el dibujo a composables. La lógica intenta mantener los efectos de sistema fuera de los " +
                "componentes visuales para evitar duplicación y estados inconsistentes. Entre sus símbolos más relevantes están fun Context, fun " +
                "SyncDisplayPerformance, fun SyncUiFeedback, fun ComponentActivity. Sus imports internos muestran una conexión directa con " +
                "app/src/main/java/com/example/mynotes/performance/DisplayPerformanceController.kt, app/src/main/java/com/example/mynotes/settings/AppSettings.kt, " +
                "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt. El snapshot incluido corresponde a esta versión del proyecto y contiene 87 líneas; " +
                "puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun Context", "fun SyncDisplayPerformance", "fun SyncUiFeedback", "fun ComponentActivity"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/performance/DisplayPerformanceController.kt", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt"), lineCount = 87, assetName = "src_app_src_main_java_com_example_mynotes_appactivitysupport_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Núcleo y actividad", title = "MainActivity.kt",
            path = "app/src/main/java/com/example/mynotes/MainActivity.kt",
            purpose = "Es el coordinador principal de MyNotes. Recibe intents de lanzamiento/compartir, crea ViewModels, aplica tema y configuración, mantiene el estado de navegación y decide qué pantalla Compose se muestra.",
            whereInApp = "Toda la navegación principal de Mis notas, Ajustes, Editor, Dibujo, Recordatorios, Detalle, Información de desarrollo y Código de desarrollo pasa por este archivo.",
            details = "Es el coordinador principal de MyNotes. Recibe intents de lanzamiento/compartir, crea ViewModels, aplica tema y configuración, mantiene el estado de " +
                "navegación y decide qué pantalla Compose se muestra. En ejecución, este archivo se sitúa por encima de las pantallas: prepara el contexto/ventana o " +
                "coordina destinos antes de delegar el dibujo a composables. La lógica intenta mantener los efectos de sistema fuera de los componentes visuales para " +
                "evitar duplicación y estados inconsistentes. Entre sus símbolos más relevantes están class AppDestination, class NavigationSnapshot, class " +
                "MainActivity, fun handleReminderIntent, fun prepareWidgetNavigation, fun handleWidgetIntent. Sus imports internos muestran una conexión directa con " +
                "app/src/main/java/com/example/mynotes/data/AppDatabase.kt, app/src/main/java/com/example/mynotes/data/Attachment.kt, " +
                "app/src/main/java/com/example/mynotes/data/Note.kt, app/src/main/java/com/example/mynotes/data/PendingAttachment.kt, " +
                "app/src/main/java/com/example/mynotes/performance/DisplayPerformanceController.kt. El snapshot incluido corresponde a esta versión del proyecto y " +
                "contiene 895 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class AppDestination", "class NavigationSnapshot", "class MainActivity", "fun handleReminderIntent", "fun prepareWidgetNavigation", "fun handleWidgetIntent", "fun handleIncomingShare", "fun clearPendingShare"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/data/AppDatabase.kt", "app/src/main/java/com/example/mynotes/data/Attachment.kt", "app/src/main/java/com/example/mynotes/data/Note.kt", "app/src/main/java/com/example/mynotes/data/PendingAttachment.kt", "app/src/main/java/com/example/mynotes/performance/DisplayPerformanceController.kt", "app/src/main/java/com/example/mynotes/reminders/ReminderRepository.kt", "app/src/main/java/com/example/mynotes/reminders/ReminderFeedbackPreferences.kt", "app/src/main/java/com/example/mynotes/reminders/ReminderReceiver.kt"), lineCount = 895, assetName = "src_app_src_main_java_com_example_mynotes_mainactivity_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Datos y persistencia", title = "AppDataBackupManager.kt",
            path = "app/src/main/java/com/example/mynotes/data/AppDataBackupManager.kt",
            purpose = "Implementa exportación e importación de copias de seguridad. Empaqueta notas, adjuntos, metadatos, preferencias y perfil en un ZIP; al restaurar valida formato, límites, rutas y versión antes de tocar la base local.",
            whereInApp = "Configuración → Copia de seguridad y restauración. Su trabajo pesado se ejecuta en Dispatchers.IO para no bloquear la interfaz.",
            details = "Implementa exportación e importación de copias de seguridad. Empaqueta notas, adjuntos, metadatos, preferencias y perfil en un ZIP; al restaurar " +
                "valida formato, límites, rutas y versión antes de tocar la base local. La capa de datos no dibuja UI. Sus entidades definen qué se guarda y sus " +
                "DAOs/repositorios traducen operaciones de alto nivel a Room/archivos. Las llamadas potencialmente costosas se consumen desde coroutines para que la UI " +
                "siga reactiva. Entre sus símbolos más relevantes están object AppDataBackupManager, class BackupSummary, fun exportBackup, fun importBackup, fun " +
                "writeUriEntry, fun extractBackup. Sus imports internos muestran una conexión directa con " +
                "app/src/main/java/com/example/mynotes/settings/AppSettings.kt, app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt, " +
                "app/src/main/java/com/example/mynotes/widget/MyNotesWidgetUpdater.kt, com.example.mynotes.util.openUriStream. El snapshot incluido corresponde a esta " +
                "versión del proyecto y contiene 492 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object AppDataBackupManager", "class BackupSummary", "fun exportBackup", "fun importBackup", "fun writeUriEntry", "fun extractBackup", "fun safeStageFile", "fun safeFileName"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt", "app/src/main/java/com/example/mynotes/widget/MyNotesWidgetUpdater.kt", "com.example.mynotes.util.openUriStream"), lineCount = 492, assetName = "src_app_src_main_java_com_example_mynotes_data_appdatabackupmanager_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Datos y persistencia", title = "AppDatabase.kt",
            path = "app/src/main/java/com/example/mynotes/data/AppDatabase.kt",
            purpose = "Define la base Room, sus DAOs, versión de esquema, migraciones y construcción singleton. Es el punto donde el almacenamiento SQLite de notas/adjuntos se abre de forma segura una sola vez.",
            whereInApp = "Es usado por ViewModels, backups, widgets y repositorios que necesitan leer o modificar notas y adjuntos.",
            details = "Define la base Room, sus DAOs, versión de esquema, migraciones y construcción singleton. Es el punto donde el almacenamiento SQLite de notas/adjuntos " +
                "se abre de forma segura una sola vez. La capa de datos no dibuja UI. Sus entidades definen qué se guarda y sus DAOs/repositorios traducen operaciones " +
                "de alto nivel a Room/archivos. Las llamadas potencialmente costosas se consumen desde coroutines para que la UI siga reactiva. Entre sus símbolos más " +
                "relevantes están class AppDatabase, fun noteDao, fun attachmentDao, fun migrate, fun getDatabase. El snapshot incluido corresponde a esta versión del " +
                "proyecto y contiene 123 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class AppDatabase", "fun noteDao", "fun attachmentDao", "fun migrate", "fun getDatabase"),
            internalDependencies = listOf(), lineCount = 123, assetName = "src_app_src_main_java_com_example_mynotes_data_appdatabase_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Datos y persistencia", title = "Attachment.kt",
            path = "app/src/main/java/com/example/mynotes/data/Attachment.kt",
            purpose = "Entidad Room que representa un archivo asociado a una nota. Conserva id, noteId, tipo, URI/ruta, nombre y fecha para reconstruir la relación entre nota y contenido multimedia.",
            whereInApp = "Aparece detrás de imágenes, vídeos, audios, documentos y otros adjuntos en tarjetas, detalle, editor, backups y widgets.",
            details = "Entidad Room que representa un archivo asociado a una nota. Conserva id, noteId, tipo, URI/ruta, nombre y fecha para reconstruir la relación entre " +
                "nota y contenido multimedia. La capa de datos no dibuja UI. Sus entidades definen qué se guarda y sus DAOs/repositorios traducen operaciones de alto " +
                "nivel a Room/archivos. Las llamadas potencialmente costosas se consumen desde coroutines para que la UI siga reactiva. Entre sus símbolos más " +
                "relevantes están class Attachment. El snapshot incluido corresponde a esta versión del proyecto y contiene 24 líneas; puede recorrerse dentro de la " +
                "app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class Attachment"),
            internalDependencies = listOf(), lineCount = 24, assetName = "src_app_src_main_java_com_example_mynotes_data_attachment_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Datos y persistencia", title = "AttachmentDao.kt",
            path = "app/src/main/java/com/example/mynotes/data/AttachmentDao.kt",
            purpose = "DAO de Room para consultar, insertar, restaurar y eliminar adjuntos. Expone tanto Flows reactivos como funciones suspend/síncronas necesarias para backup, widgets y operaciones puntuales.",
            whereInApp = "Lo consume NoteViewModel, AppDataBackupManager, pantallas que observan adjuntos y procesos de widget.",
            details = "DAO de Room para consultar, insertar, restaurar y eliminar adjuntos. Expone tanto Flows reactivos como funciones suspend/síncronas necesarias para " +
                "backup, widgets y operaciones puntuales. La capa de datos no dibuja UI. Sus entidades definen qué se guarda y sus DAOs/repositorios traducen " +
                "operaciones de alto nivel a Room/archivos. Las llamadas potencialmente costosas se consumen desde coroutines para que la UI siga reactiva. Entre sus " +
                "símbolos más relevantes están interface AttachmentDao, fun getAllAttachments, fun getAllAttachmentsOnce, fun insertAttachmentsForRestore, fun " +
                "deleteAllAttachments, fun getAttachments. El snapshot incluido corresponde a esta versión del proyecto y contiene 69 líneas; puede recorrerse dentro " +
                "de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("interface AttachmentDao", "fun getAllAttachments", "fun getAllAttachmentsOnce", "fun insertAttachmentsForRestore", "fun deleteAllAttachments", "fun getAttachments", "fun getAttachmentsOnce", "fun insertAttachment"),
            internalDependencies = listOf(), lineCount = 69, assetName = "src_app_src_main_java_com_example_mynotes_data_attachmentdao_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Datos y persistencia", title = "Note.kt",
            path = "app/src/main/java/com/example/mynotes/data/Note.kt",
            purpose = "Entidad principal de Room para una nota. Reúne texto, color, prioridad, categoría, favorito, fijado y demás estado persistente que alimenta la UI y los widgets.",
            whereInApp = "Cada tarjeta de Mis notas, el editor, el detalle, filtros y widgets se construyen a partir de esta entidad.",
            details = "Entidad principal de Room para una nota. Reúne texto, color, prioridad, categoría, favorito, fijado y demás estado persistente que alimenta la UI y " +
                "los widgets. La capa de datos no dibuja UI. Sus entidades definen qué se guarda y sus DAOs/repositorios traducen operaciones de alto nivel a " +
                "Room/archivos. Las llamadas potencialmente costosas se consumen desde coroutines para que la UI siga reactiva. Entre sus símbolos más relevantes están " +
                "class Note. El snapshot incluido corresponde a esta versión del proyecto y contiene 47 líneas; puede recorrerse dentro de la app en bloques de 160 " +
                "líneas hasta visualizarlo completo.",
            keySymbols = listOf("class Note"),
            internalDependencies = listOf(), lineCount = 47, assetName = "src_app_src_main_java_com_example_mynotes_data_note_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Datos y persistencia", title = "NoteDao.kt",
            path = "app/src/main/java/com/example/mynotes/data/NoteDao.kt",
            purpose = "DAO central de notas. Incluye consultas reactivas y de una sola lectura, estadísticas para widgets, operaciones de insert/update/delete y consultas especializadas por favorito, fijado, categoría o prioridad.",
            whereInApp = "Lo usan NoteViewModel, widgets, backups y acciones rápidas que necesitan acceder a la colección de notas.",
            details = "DAO central de notas. Incluye consultas reactivas y de una sola lectura, estadísticas para widgets, operaciones de insert/update/delete y consultas " +
                "especializadas por favorito, fijado, categoría o prioridad. La capa de datos no dibuja UI. Sus entidades definen qué se guarda y sus DAOs/repositorios " +
                "traducen operaciones de alto nivel a Room/archivos. Las llamadas potencialmente costosas se consumen desde coroutines para que la UI siga reactiva. " +
                "Entre sus símbolos más relevantes están class WidgetNoteStats, interface NoteDao, fun getAllNotes, fun getAllNotesOnce, fun getNoteByIdOnce, fun " +
                "getRecentNotesForWidget. El snapshot incluido corresponde a esta versión del proyecto y contiene 142 líneas; puede recorrerse dentro de la app en " +
                "bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class WidgetNoteStats", "interface NoteDao", "fun getAllNotes", "fun getAllNotesOnce", "fun getNoteByIdOnce", "fun getRecentNotesForWidget", "fun getNoteCountForWidget", "fun getFavoriteCountForWidget"),
            internalDependencies = listOf(), lineCount = 142, assetName = "src_app_src_main_java_com_example_mynotes_data_notedao_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Datos y persistencia", title = "PendingAttachment.kt",
            path = "app/src/main/java/com/example/mynotes/data/PendingAttachment.kt",
            purpose = "Modelo transitorio para un adjunto que todavía no está ligado a un noteId definitivo. Permite que el editor reúna archivos antes de insertar/actualizar la nota.",
            whereInApp = "Se utiliza mientras se crea o edita una nota y durante el paso de datos desde selectores de archivos hacia NoteViewModel.",
            details = "Modelo transitorio para un adjunto que todavía no está ligado a un noteId definitivo. Permite que el editor reúna archivos antes de " +
                "insertar/actualizar la nota. La capa de datos no dibuja UI. Sus entidades definen qué se guarda y sus DAOs/repositorios traducen operaciones de alto " +
                "nivel a Room/archivos. Las llamadas potencialmente costosas se consumen desde coroutines para que la UI siga reactiva. Entre sus símbolos más " +
                "relevantes están class PendingAttachment. El snapshot incluido corresponde a esta versión del proyecto y contiene 21 líneas; puede recorrerse dentro " +
                "de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class PendingAttachment"),
            internalDependencies = listOf(), lineCount = 21, assetName = "src_app_src_main_java_com_example_mynotes_data_pendingattachment_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Enlaces", title = "LinkPreviewRepository.kt",
            path = "app/src/main/java/com/example/mynotes/links/LinkPreviewRepository.kt",
            purpose = "Normaliza URLs y concentra reglas mínimas compartidas para enlaces antes de generar previews. Evita que diferentes pantallas traten esquemas, hosts y formatos de URL de forma inconsistente.",
            whereInApp = "Lo utiliza la infraestructura de tarjetas de enlace y cualquier flujo que necesite convertir texto/URL en una dirección válida para preview.",
            details = "Normaliza URLs y concentra reglas mínimas compartidas para enlaces antes de generar previews. Evita que diferentes pantallas traten esquemas, hosts y " +
                "formatos de URL de forma inconsistente. Esta pieza forma parte del pipeline de enlaces: normaliza o recupera información antes de que la capa Compose " +
                "decida cómo presentarla. Separar estas reglas evita que editor, detalle y tarjetas interpreten la misma URL de forma distinta. Entre sus símbolos más " +
                "relevantes están object LinkPreviewRepository, fun normalizeUrl. El snapshot incluido corresponde a esta versión del proyecto y contiene 31 líneas; " +
                "puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object LinkPreviewRepository", "fun normalizeUrl"),
            internalDependencies = listOf(), lineCount = 31, assetName = "src_app_src_main_java_com_example_mynotes_links_linkpreviewrepository_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Rendimiento", title = "AttachmentPreviewCache.kt",
            path = "app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt",
            purpose = "Motor de previews y caché de adjuntos. Genera miniaturas de imagen/vídeo/documento/audio, limita concurrencia, adapta resolución/calidad al perfil de rendimiento y reutiliza resultados para evitar decodificaciones repetidas.",
            whereInApp = "Tarjetas de notas, adjuntos inline, visor, widgets y otras superficies que muestran previews dependen de este caché.",
            details = "Motor de previews y caché de adjuntos. Genera miniaturas de imagen/vídeo/documento/audio, limita concurrencia, adapta resolución/calidad al perfil de " +
                "rendimiento y reutiliza resultados para evitar decodificaciones repetidas. Su objetivo es evitar trabajo repetido o adaptar el coste al perfil " +
                "elegido. El archivo conserva/calcule estado reutilizable y expone una API pequeña para que las pantallas no implementen optimizaciones localmente. " +
                "Entre sus símbolos más relevantes están object AttachmentPreviewCache, class MediaPreview, class PreviewProfile, fun previewProfile, fun sizeOf, fun " +
                "memoryBitmap. Sus imports internos muestran una conexión directa con com.example.mynotes.util.exifOrientationMatrix, " +
                "com.example.mynotes.util.openUriStream. El snapshot incluido corresponde a esta versión del proyecto y contiene 773 líneas; puede recorrerse dentro de " +
                "la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object AttachmentPreviewCache", "class MediaPreview", "class PreviewProfile", "fun previewProfile", "fun sizeOf", "fun memoryBitmap", "fun rememberBitmap", "fun preferredTags"),
            internalDependencies = listOf("com.example.mynotes.util.exifOrientationMatrix", "com.example.mynotes.util.openUriStream"), lineCount = 773, assetName = "src_app_src_main_java_com_example_mynotes_performance_attachmentpreviewcache_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Rendimiento", title = "DisplayPerformanceController.kt",
            path = "app/src/main/java/com/example/mynotes/performance/DisplayPerformanceController.kt",
            purpose = "Aplica preferencias de frecuencia de refresco/Frame Rate a la ventana según el modo rendimiento, equilibrado o calidad. Recuerda la última solicitud y puede reaplicarla al recuperar foco.",
            whereInApp = "Afecta la fluidez global de la app, especialmente desplazamientos y animaciones en dispositivos con pantallas de alta frecuencia.",
            details = "Aplica preferencias de frecuencia de refresco/Frame Rate a la ventana según el modo rendimiento, equilibrado o calidad. Recuerda la última solicitud y " +
                "puede reaplicarla al recuperar foco. Su objetivo es evitar trabajo repetido o adaptar el coste al perfil elegido. El archivo conserva/calcule estado " +
                "reutilizable y expone una API pequeña para que las pantallas no implementen optimizaciones localmente. Entre sus símbolos más relevantes están object " +
                "DisplayPerformanceController, fun requestForPerformanceMode, fun reapplyLastRequest, fun release, fun refreshRateFor, fun requestRefreshRate. Sus " +
                "imports internos muestran una conexión directa con app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt. El snapshot incluido corresponde a " +
                "esta versión del proyecto y contiene 118 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object DisplayPerformanceController", "fun requestForPerformanceMode", "fun reapplyLastRequest", "fun release", "fun refreshRateFor", "fun requestRefreshRate", "fun chooseClosestMode"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt"), lineCount = 118, assetName = "src_app_src_main_java_com_example_mynotes_performance_displayperformancecontroller_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Recordatorios", title = "Reminder.kt",
            path = "app/src/main/java/com/example/mynotes/reminders/Reminder.kt",
            purpose = "Modelo persistible de recordatorio con título, descripción, fecha/hora, repetición, prioridad, color, estado habilitado y fecha de creación.",
            whereInApp = "Alimenta la lista y editor de Recordatorios, el scheduler de alarmas y la notificación que finalmente recibe el usuario.",
            details = "Modelo persistible de recordatorio con título, descripción, fecha/hora, repetición, prioridad, color, estado habilitado y fecha de creación. Forma " +
                "parte del flujo persistencia → programación del sistema → recepción → notificación. La separación permite que un recordatorio siga funcionando aunque " +
                "la pantalla de Compose ya no esté abierta. Entre sus símbolos más relevantes están class Reminder. El snapshot incluido corresponde a esta versión del " +
                "proyecto y contiene 25 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class Reminder"),
            internalDependencies = listOf(), lineCount = 25, assetName = "src_app_src_main_java_com_example_mynotes_reminders_reminder_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Recordatorios", title = "ReminderAlarmScheduler.kt",
            path = "app/src/main/java/com/example/mynotes/reminders/ReminderAlarmScheduler.kt",
            purpose = "Traduce un Reminder a una alarma del sistema mediante AlarmManager/PendingIntent y también puede cancelarla. Encapsula las flags y la identidad del PendingIntent para que programar/cancelar sea simétrico.",
            whereInApp = "Se invoca al crear, editar, habilitar, deshabilitar o reprogramar recordatorios.",
            details = "Traduce un Reminder a una alarma del sistema mediante AlarmManager/PendingIntent y también puede cancelarla. Encapsula las flags y la identidad del " +
                "PendingIntent para que programar/cancelar sea simétrico. Forma parte del flujo persistencia → programación del sistema → recepción → notificación. La " +
                "separación permite que un recordatorio siga funcionando aunque la pantalla de Compose ya no esté abierta. Entre sus símbolos más relevantes están " +
                "object ReminderAlarmScheduler, fun schedule, fun cancel, fun pendingIntent. El snapshot incluido corresponde a esta versión del proyecto y contiene 53 " +
                "líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object ReminderAlarmScheduler", "fun schedule", "fun cancel", "fun pendingIntent"),
            internalDependencies = listOf(), lineCount = 53, assetName = "src_app_src_main_java_com_example_mynotes_reminders_reminderalarmscheduler_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Recordatorios", title = "ReminderFeedbackPreferences.kt",
            path = "app/src/main/java/com/example/mynotes/reminders/ReminderFeedbackPreferences.kt",
            purpose = "Lee las preferencias de sonido, volumen, tono y hápticos que deben utilizar las notificaciones/alertas de recordatorio, independientemente del estado Compose de Ajustes.",
            whereInApp = "La usa ReminderReceiver cuando el recordatorio se dispara fuera de la interfaz o incluso con la app cerrada.",
            details = "Lee las preferencias de sonido, volumen, tono y hápticos que deben utilizar las notificaciones/alertas de recordatorio, independientemente del estado " +
                "Compose de Ajustes. Forma parte del flujo persistencia → programación del sistema → recepción → notificación. La separación permite que un " +
                "recordatorio siga funcionando aunque la pantalla de Compose ya no esté abierta. Entre sus símbolos más relevantes están object " +
                "ReminderFeedbackPreferences, class Snapshot, fun sync, fun read, fun playReminderAlert, fun previewRingtone. Sus imports internos muestran una " +
                "conexión directa con com.example.mynotes.R, app/src/main/java/com/example/mynotes/settings/AppSettings.kt, " +
                "app/src/main/java/com/example/mynotes/settings/FeedbackPreferencePolicy.kt, com.example.mynotes.ui.sound.UiHaptic, " +
                "app/src/main/java/com/example/mynotes/ui/sound/UiHapticPlayer.kt. El snapshot incluido corresponde a esta versión del proyecto y contiene 229 líneas; " +
                "puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object ReminderFeedbackPreferences", "class Snapshot", "fun sync", "fun read", "fun playReminderAlert", "fun previewRingtone", "fun playRingtoneInternal"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "app/src/main/java/com/example/mynotes/settings/FeedbackPreferencePolicy.kt", "com.example.mynotes.ui.sound.UiHaptic", "app/src/main/java/com/example/mynotes/ui/sound/UiHapticPlayer.kt", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt", "app/src/main/java/com/example/mynotes/ui/theme/PaletteCatalog.kt", "com.example.mynotes.ui.theme.resolveAppColorScheme"), lineCount = 229, assetName = "src_app_src_main_java_com_example_mynotes_reminders_reminderfeedbackpreferences_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Recordatorios", title = "ReminderReceiver.kt",
            path = "app/src/main/java/com/example/mynotes/reminders/ReminderReceiver.kt",
            purpose = "BroadcastReceiver que recibe la alarma, recupera el recordatorio, crea/actualiza el canal de notificación y publica la notificación con sonido/háptico y PendingIntent de apertura.",
            whereInApp = "Funciona en segundo plano cuando llega la hora de un recordatorio y también participa tras eventos de reinicio/actualización cuando corresponda.",
            details = "BroadcastReceiver que recibe la alarma, recupera el recordatorio, crea/actualiza el canal de notificación y publica la notificación con sonido/háptico " +
                "y PendingIntent de apertura. Forma parte del flujo persistencia → programación del sistema → recepción → notificación. La separación permite que un " +
                "recordatorio siga funcionando aunque la pantalla de Compose ya no esté abierta. Entre sus símbolos más relevantes están class ReminderReceiver, fun " +
                "onReceive, fun showNotification, fun buildCustomView, fun metadata, fun reminderAccent. Sus imports internos muestran una conexión directa con " +
                "app/src/main/java/com/example/mynotes/MainActivity.kt, com.example.mynotes.R. El snapshot incluido corresponde a esta versión del proyecto y contiene " +
                "250 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class ReminderReceiver", "fun onReceive", "fun showNotification", "fun buildCustomView", "fun metadata", "fun reminderAccent", "fun localizedContext"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/MainActivity.kt", "com.example.mynotes.R"), lineCount = 250, assetName = "src_app_src_main_java_com_example_mynotes_reminders_reminderreceiver_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Recordatorios", title = "ReminderRepository.kt",
            path = "app/src/main/java/com/example/mynotes/reminders/ReminderRepository.kt",
            purpose = "Persistencia de recordatorios basada en preferencias serializadas. Mantiene un StateFlow actualizado, escucha cambios externos y coordina alta/edición/eliminación con ReminderAlarmScheduler.",
            whereInApp = "ReminderScreen observa este repositorio y cualquier cambio queda sincronizado con las alarmas del sistema.",
            details = "Persistencia de recordatorios basada en preferencias serializadas. Mantiene un StateFlow actualizado, escucha cambios externos y coordina " +
                "alta/edición/eliminación con ReminderAlarmScheduler. Forma parte del flujo persistencia → programación del sistema → recepción → notificación. La " +
                "separación permite que un recordatorio siga funcionando aunque la pantalla de Compose ya no esté abierta. Entre sus símbolos más relevantes están " +
                "class ReminderRepository, fun getReminder, fun upsert, fun delete, fun setEnabled, fun advanceAfterTrigger. El snapshot incluido corresponde a esta " +
                "versión del proyecto y contiene 225 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class ReminderRepository", "fun getReminder", "fun upsert", "fun delete", "fun setEnabled", "fun advanceAfterTrigger", "fun rescheduleAll", "fun save"),
            internalDependencies = listOf(), lineCount = 225, assetName = "src_app_src_main_java_com_example_mynotes_reminders_reminderrepository_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Preferencias y políticas", title = "AppSettings.kt",
            path = "app/src/main/java/com/example/mynotes/settings/AppSettings.kt",
            purpose = "Data class que agrupa el estado completo de personalización y comportamiento de MyNotes: tema, colores, tipografía, animaciones, menús, sonidos, hápticos, rendimiento, PDF y muchas otras opciones.",
            whereInApp = "Es el contrato que viaja desde SettingsRepository/SettingsViewModel hacia casi todas las pantallas y componentes.",
            details = "Data class que agrupa el estado completo de personalización y comportamiento de MyNotes: tema, colores, tipografía, animaciones, menús, sonidos, " +
                "hápticos, rendimiento, PDF y muchas otras opciones. Esta capa define valores admitidos y persistencia, mientras la UI sólo envía intenciones de " +
                "cambio. Así una preferencia restaurada o heredada se normaliza en un único sitio antes de llegar al tema o a los componentes. Entre sus símbolos más " +
                "relevantes están class AppSettings. El snapshot incluido corresponde a esta versión del proyecto y contiene 159 líneas; puede recorrerse dentro de la " +
                "app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class AppSettings"),
            internalDependencies = listOf(), lineCount = 159, assetName = "src_app_src_main_java_com_example_mynotes_settings_appsettings_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Preferencias y políticas", title = "DeveloperFeatures.kt",
            path = "app/src/main/java/com/example/mynotes/settings/DeveloperFeatures.kt",
            purpose = "Guarda flags internos de funciones de desarrollo, actualmente el desbloqueo de Google Sans Flex. Está separado de AppSettings para que estas opciones ocultas no contaminen la configuración normal.",
            whereInApp = "Información de desarrollo usa este archivo cuando se activa/desbloquea una función de desarrollador.",
            details = "Guarda flags internos de funciones de desarrollo, actualmente el desbloqueo de Google Sans Flex. Está separado de AppSettings para que estas opciones " +
                "ocultas no contaminen la configuración normal. Esta capa define valores admitidos y persistencia, mientras la UI sólo envía intenciones de cambio. Así " +
                "una preferencia restaurada o heredada se normaliza en un único sitio antes de llegar al tema o a los componentes. Entre sus símbolos más relevantes " +
                "están object DeveloperFeatures, fun isGoogleSansFlexUnlocked, fun unlockGoogleSansFlex. El snapshot incluido corresponde a esta versión del proyecto y " +
                "contiene 23 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object DeveloperFeatures", "fun isGoogleSansFlexUnlocked", "fun unlockGoogleSansFlex"),
            internalDependencies = listOf(), lineCount = 23, assetName = "src_app_src_main_java_com_example_mynotes_settings_developerfeatures_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Preferencias y políticas", title = "FeedbackPreferencePolicy.kt",
            path = "app/src/main/java/com/example/mynotes/settings/FeedbackPreferencePolicy.kt",
            purpose = "Catálogo y normalización de temas sonoros, estilos hápticos y tonos de recordatorio. Mantiene claves estables y recursos asociados para que preferencias antiguas sigan teniendo una salida válida.",
            whereInApp = "Ajustes, UiSoundPlayer, UiHapticPlayer y recordatorios consultan estas políticas.",
            details = "Catálogo y normalización de temas sonoros, estilos hápticos y tonos de recordatorio. Mantiene claves estables y recursos asociados para que " +
                "preferencias antiguas sigan teniendo una salida válida. Esta capa define valores admitidos y persistencia, mientras la UI sólo envía intenciones de " +
                "cambio. Así una preferencia restaurada o heredada se normaliza en un único sitio antes de llegar al tema o a los componentes. Entre sus símbolos más " +
                "relevantes están object FeedbackPreferencePolicy, class SoundThemeResources, class HapticStyle, class ReminderTone, fun normalizeSoundTheme, fun " +
                "normalizeHapticStyle. Sus imports internos muestran una conexión directa con com.example.mynotes.R. El snapshot incluido corresponde a esta versión " +
                "del proyecto y contiene 115 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object FeedbackPreferencePolicy", "class SoundThemeResources", "class HapticStyle", "class ReminderTone", "fun normalizeSoundTheme", "fun normalizeHapticStyle", "fun normalizeReminderRingtone", "fun reminderTone"),
            internalDependencies = listOf("com.example.mynotes.R"), lineCount = 115, assetName = "src_app_src_main_java_com_example_mynotes_settings_feedbackpreferencepolicy_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Preferencias y políticas", title = "FontPreferencePolicy.kt",
            path = "app/src/main/java/com/example/mynotes/settings/FontPreferencePolicy.kt",
            purpose = "Define las claves de fuentes admitidas y normaliza claves heredadas. Evita que backups o versiones antiguas de la app dejen una fuente no reconocida en AppSettings.",
            whereInApp = "Se aplica al leer/escribir la fuente en Ajustes y al resolver appFontFamily.",
            details = "Define las claves de fuentes admitidas y normaliza claves heredadas. Evita que backups o versiones antiguas de la app dejen una fuente no reconocida " +
                "en AppSettings. Esta capa define valores admitidos y persistencia, mientras la UI sólo envía intenciones de cambio. Así una preferencia restaurada o " +
                "heredada se normaliza en un único sitio antes de llegar al tema o a los componentes. Entre sus símbolos más relevantes están object " +
                "FontPreferencePolicy, fun normalize. El snapshot incluido corresponde a esta versión del proyecto y contiene 51 líneas; puede recorrerse dentro de la " +
                "app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object FontPreferencePolicy", "fun normalize"),
            internalDependencies = listOf(), lineCount = 51, assetName = "src_app_src_main_java_com_example_mynotes_settings_fontpreferencepolicy_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Preferencias y políticas", title = "MenuPreferencePolicy.kt",
            path = "app/src/main/java/com/example/mynotes/settings/MenuPreferencePolicy.kt",
            purpose = "Concentra las opciones configurables del menú de nota, prioridades y colores, junto con sus claves canónicas y orden. Sirve como contrato común entre Ajustes y los menús reales.",
            whereInApp = "Afecta los menús de tarjetas/detalle y la sección de personalización del menú en Ajustes.",
            details = "Concentra las opciones configurables del menú de nota, prioridades y colores, junto con sus claves canónicas y orden. Sirve como contrato común entre " +
                "Ajustes y los menús reales. Esta capa define valores admitidos y persistencia, mientras la UI sólo envía intenciones de cambio. Así una preferencia " +
                "restaurada o heredada se normaliza en un único sitio antes de llegar al tema o a los componentes. Entre sus símbolos más relevantes están object " +
                "MenuPreferencePolicy, class PriorityOption, class ColorOption, fun orderedKeys, fun normalizeOrder, fun hiddenKeys. Sus imports internos muestran una " +
                "conexión directa con com.example.mynotes.R. El snapshot incluido corresponde a esta versión del proyecto y contiene 56 líneas; puede recorrerse dentro " +
                "de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object MenuPreferencePolicy", "class PriorityOption", "class ColorOption", "fun orderedKeys", "fun normalizeOrder", "fun hiddenKeys", "fun normalizeHiddenItems", "fun collectKeys"),
            internalDependencies = listOf("com.example.mynotes.R"), lineCount = 56, assetName = "src_app_src_main_java_com_example_mynotes_settings_menupreferencepolicy_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Preferencias y políticas", title = "SettingsRepository.kt",
            path = "app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt",
            purpose = "Repositorio DataStore de toda la configuración. Convierte Preferences a AppSettings, aplica defaults/normalización/rangos y ofrece setters suspend para actualizar cada preferencia de forma transaccional.",
            whereInApp = "Es la fuente persistente de la pantalla Ajustes y del SettingsViewModel; sus valores llegan al tema y a las funciones globales.",
            details = "Repositorio DataStore de toda la configuración. Convierte Preferences a AppSettings, aplica defaults/normalización/rangos y ofrece setters suspend " +
                "para actualizar cada preferencia de forma transaccional. Esta capa define valores admitidos y persistencia, mientras la UI sólo envía intenciones de " +
                "cambio. Así una preferencia restaurada o heredada se normaliza en un único sitio antes de llegar al tema o a los componentes. Entre sus símbolos más " +
                "relevantes están class SettingsRepository, fun setConfigurationMode, fun setDarkMode, fun setBackgroundColor, fun setBackgroundToneIndex, fun " +
                "setBackgroundIntensity. Sus imports internos muestran una conexión directa con app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt, " +
                "app/src/main/java/com/example/mynotes/ui/theme/PaletteCatalog.kt. El snapshot incluido corresponde a esta versión del proyecto y contiene 428 líneas; " +
                "puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class SettingsRepository", "fun setConfigurationMode", "fun setDarkMode", "fun setBackgroundColor", "fun setBackgroundToneIndex", "fun setBackgroundIntensity", "fun setSettingsPanelTone", "fun setSurfacePanelIntensity"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt", "app/src/main/java/com/example/mynotes/ui/theme/PaletteCatalog.kt"), lineCount = 428, assetName = "src_app_src_main_java_com_example_mynotes_settings_settingsrepository_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas principales", title = "AttachmentViewerActivity.kt",
            path = "app/src/main/java/com/example/mynotes/ui/AttachmentViewerActivity.kt",
            purpose = "Visor dedicado para abrir adjuntos fuera de la tarjeta/editor. Selecciona la representación adecuada para imagen, vídeo, audio, texto/documentos y archivos genéricos, manteniendo tema, gestos y reproducción.",
            whereInApp = "Se abre desde una nota cuando el usuario toca un adjunto para verlo con mayor tamaño o controles propios.",
            details = "Visor dedicado para abrir adjuntos fuera de la tarjeta/editor. Selecciona la representación adecuada para imagen, vídeo, audio, texto/documentos y " +
                "archivos genéricos, manteniendo tema, gestos y reproducción. El archivo combina estado de Activity/Android con contenido Compose y delega tareas " +
                "especializadas a cachés, reproductores o utilidades. Su ciclo de vida controla recursos que no deben sobrevivir a la pantalla. Entre sus símbolos más " +
                "relevantes están fun openAttachmentViewer, class AttachmentViewerActivity, fun onWindowFocusChanged, fun onResume, fun onMultiWindowModeChanged, fun " +
                "onDestroy. Sus imports internos muestran una conexión directa con com.example.mynotes.R, " +
                "app/src/main/java/com/example/mynotes/ui/components/ScrollPositionCapsule.kt, com.example.mynotes.ui.media.BindMediaPlayer, " +
                "app/src/main/java/com/example/mynotes/ui/media/MediaPlaybackState.kt, com.example.mynotes.ui.media.MediaSeekSlider. El snapshot incluido corresponde a " +
                "esta versión del proyecto y contiene 914 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun openAttachmentViewer", "class AttachmentViewerActivity", "fun onWindowFocusChanged", "fun onResume", "fun onMultiWindowModeChanged", "fun onDestroy", "fun onCreate", "fun AttachmentViewerScreen"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/ui/components/ScrollPositionCapsule.kt", "com.example.mynotes.ui.media.BindMediaPlayer", "app/src/main/java/com/example/mynotes/ui/media/MediaPlaybackState.kt", "com.example.mynotes.ui.media.MediaSeekSlider", "com.example.mynotes.ui.media.rememberMediaPlaybackState", "com.example.mynotes.SyncDisplayPerformance", "com.example.mynotes.applyAndroidNavigationBarPolicy"), lineCount = 914, assetName = "src_app_src_main_java_com_example_mynotes_ui_attachmentvieweractivity_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "AppPopupStyles.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/AppPopupStyles.kt",
            purpose = "Define el contenedor estilizado para menús desplegables de la app y su cápsula de desplazamiento. Uniforma forma, borde, color, ancho y comportamiento de scroll en popups extensos.",
            whereInApp = "Menús de tarjetas, ajustes y otras listas emergentes reutilizan este estilo en lugar de implementar DropdownMenu de forma distinta.",
            details = "Define el contenedor estilizado para menús desplegables de la app y su cápsula de desplazamiento. Uniforma forma, borde, color, ancho y comportamiento " +
                "de scroll en popups extensos. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde AppSettings y evita modificar " +
                "almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más relevantes están fun " +
                "AppDropdownMenu, fun AppAlertDialog. Sus imports internos muestran una conexión directa con com.example.mynotes.ui.theme.ensureUiContrast. El snapshot " +
                "incluido corresponde a esta versión del proyecto y contiene 143 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo " +
                "completo.",
            keySymbols = listOf("fun AppDropdownMenu", "fun AppAlertDialog"),
            internalDependencies = listOf("com.example.mynotes.ui.theme.ensureUiContrast"), lineCount = 143, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_apppopupstyles_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "AttachmentPreviewTile.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/AttachmentPreviewTile.kt",
            purpose = "Renderiza miniaturas compactas de adjuntos y decide qué representación usar según tipo. Coordina retraso de carga, caché de preview, placeholders y fallback de iconos.",
            whereInApp = "Aparece principalmente en editor/detalle y tarjetas donde se necesita una previsualización pequeña de un archivo.",
            details = "Renderiza miniaturas compactas de adjuntos y decide qué representación usar según tipo. Coordina retraso de carga, caché de preview, placeholders y " +
                "fallback de iconos. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde AppSettings y evita modificar " +
                "almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más relevantes están fun " +
                "AttachmentPreviewTile, fun rememberPreviewReady, fun ImageAttachment, fun VideoAttachment, fun AudioAttachment, fun FileAttachment. Sus imports " +
                "internos muestran una conexión directa con com.example.mynotes.R, app/src/main/java/com/example/mynotes/data/Attachment.kt, " +
                "app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt, com.example.mynotes.ui.media.formatMinuteSecondDuration, " +
                "com.example.mynotes.ui.sound.UiActionSound. El snapshot incluido corresponde a esta versión del proyecto y contiene 286 líneas; puede recorrerse " +
                "dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun AttachmentPreviewTile", "fun rememberPreviewReady", "fun ImageAttachment", "fun VideoAttachment", "fun AudioAttachment", "fun FileAttachment", "fun AttachmentFallback", "fun DurationBadge"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/data/Attachment.kt", "app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt", "com.example.mynotes.ui.media.formatMinuteSecondDuration", "com.example.mynotes.ui.sound.UiActionSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt", "com.example.mynotes.ui.openAttachmentViewer"), lineCount = 286, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_attachmentpreviewtile_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "BackupRestoreSection.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/BackupRestoreSection.kt",
            purpose = "Sección Compose de Ajustes para exportar/importar backups. Maneja launchers de Activity Result, estado de operación, confirmaciones y mensajes, delegando la lógica pesada a AppDataBackupManager.",
            whereInApp = "Configuración → Copia de seguridad y restauración.",
            details = "Sección Compose de Ajustes para exportar/importar backups. Maneja launchers de Activity Result, estado de operación, confirmaciones y mensajes, " +
                "delegando la lógica pesada a AppDataBackupManager. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde " +
                "AppSettings y evita modificar almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más " +
                "relevantes están fun BackupRestoreSection. Sus imports internos muestran una conexión directa con com.example.mynotes.R, " +
                "com.example.mynotes.ui.components.AppAlertDialog, app/src/main/java/com/example/mynotes/data/AppDataBackupManager.kt, " +
                "app/src/main/java/com/example/mynotes/settings/AppSettings.kt, com.example.mynotes.ui.sound.UiActionSound. El snapshot incluido corresponde a esta " +
                "versión del proyecto y contiene 160 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun BackupRestoreSection"),
            internalDependencies = listOf("com.example.mynotes.R", "com.example.mynotes.ui.components.AppAlertDialog", "app/src/main/java/com/example/mynotes/data/AppDataBackupManager.kt", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "com.example.mynotes.ui.sound.UiActionSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt"), lineCount = 160, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_backuprestoresection_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "ConfigurationModeDialog.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/ConfigurationModeDialog.kt",
            purpose = "Diálogo para elegir el modo de configuración/personalización. Calcula colores adaptativos, muestra opciones seleccionables y aplica feedback antes de confirmar el cambio.",
            whereInApp = "Se abre desde Ajustes cuando el usuario cambia entre modos de configuración disponibles.",
            details = "Diálogo para elegir el modo de configuración/personalización. Calcula colores adaptativos, muestra opciones seleccionables y aplica feedback antes de " +
                "confirmar el cambio. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde AppSettings y evita modificar " +
                "almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más relevantes están fun " +
                "ConfigurationModeDialog, fun ConfigurationModeChoiceButton. Sus imports internos muestran una conexión directa con com.example.mynotes.R, " +
                "com.example.mynotes.ui.sound.UiActionSound, app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt, " +
                "com.example.mynotes.ui.theme.rememberAdaptiveUiButtonColors, com.example.mynotes.ui.theme.automaticUiTextColor. El snapshot incluido corresponde a " +
                "esta versión del proyecto y contiene 267 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun ConfigurationModeDialog", "fun ConfigurationModeChoiceButton"),
            internalDependencies = listOf("com.example.mynotes.R", "com.example.mynotes.ui.sound.UiActionSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt", "com.example.mynotes.ui.theme.rememberAdaptiveUiButtonColors", "com.example.mynotes.ui.theme.automaticUiTextColor", "com.example.mynotes.ui.theme.ensureUiContrast", "com.example.mynotes.ui.theme.softenUiColorToContrast"), lineCount = 267, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_configurationmodedialog_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "ExtremeCustomizationSection.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/ExtremeCustomizationSection.kt",
            purpose = "Agrupa controles de personalización avanzada: iconos, movimiento, easing, rendimiento, acentos y otros parámetros extremos. Reutiliza filas/toggles compartidos para conservar consistencia visual.",
            whereInApp = "Ajustes avanzados/extremos; sus callbacks escriben en SettingsViewModel.",
            details = "Agrupa controles de personalización avanzada: iconos, movimiento, easing, rendimiento, acentos y otros parámetros extremos. Reutiliza filas/toggles " +
                "compartidos para conservar consistencia visual. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde AppSettings " +
                "y evita modificar almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más relevantes " +
                "están class AccentOption, class SettingsChoiceOption, fun ExtremeCustomizationHeader, fun ExtremeIconsSettingsSection, fun " +
                "ExtremeAccentSettingsSection, fun ExtremeNoteCardsSettingsSection. Sus imports internos muestran una conexión directa con com.example.mynotes.R, " +
                "app/src/main/java/com/example/mynotes/settings/AppSettings.kt, com.example.mynotes.ui.components.AppDropdownMenu, " +
                "com.example.mynotes.ui.motion.ConfigurableAnimatedContent, com.example.mynotes.ui.sound.UiActionSound. El snapshot incluido corresponde a esta versión " +
                "del proyecto y contiene 539 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class AccentOption", "class SettingsChoiceOption", "fun ExtremeCustomizationHeader", "fun ExtremeIconsSettingsSection", "fun ExtremeAccentSettingsSection", "fun ExtremeNoteCardsSettingsSection", "fun ExtremeFabSettingsSection", "fun ExtremePerformanceSettingsSection"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "com.example.mynotes.ui.components.AppDropdownMenu", "com.example.mynotes.ui.motion.ConfigurableAnimatedContent", "com.example.mynotes.ui.sound.UiActionSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt", "app/src/main/java/com/example/mynotes/ui/theme/PaletteCatalog.kt"), lineCount = 539, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_extremecustomizationsection_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "InlineNoteAttachment.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/InlineNoteAttachment.kt",
            purpose = "Presenta adjuntos dentro del flujo de una nota y contiene coordinación de reproducción para audio/vídeo inline. Evita múltiples reproductores activos y limpia ExoPlayer al salir de composición.",
            whereInApp = "Detalle/editor de nota cuando el contenido multimedia se muestra embebido en lugar de abrir un visor externo.",
            details = "Presenta adjuntos dentro del flujo de una nota y contiene coordinación de reproducción para audio/vídeo inline. Evita múltiples reproductores activos " +
                "y limpia ExoPlayer al salir de composición. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde AppSettings y " +
                "evita modificar almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más relevantes " +
                "están object InlinePlaybackCoordinator, fun activate, fun clear, fun InlineNoteAttachment, fun InlineImageAttachment, fun InlineVideoAttachment. Sus " +
                "imports internos muestran una conexión directa con com.example.mynotes.R, app/src/main/java/com/example/mynotes/data/Attachment.kt, " +
                "app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt, com.example.mynotes.ui.media.BindMediaPlayer, " +
                "com.example.mynotes.ui.media.MediaSeekSlider. El snapshot incluido corresponde a esta versión del proyecto y contiene 516 líneas; puede recorrerse " +
                "dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object InlinePlaybackCoordinator", "fun activate", "fun clear", "fun InlineNoteAttachment", "fun InlineImageAttachment", "fun InlineVideoAttachment", "fun InlineAudioAttachment", "fun InlinePdfAttachment"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/data/Attachment.kt", "app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt", "com.example.mynotes.ui.media.BindMediaPlayer", "com.example.mynotes.ui.media.MediaSeekSlider", "com.example.mynotes.ui.media.rememberMediaPlaybackState", "com.example.mynotes.ui.media.formatMinuteSecondDuration", "com.example.mynotes.ui.sound.UiActionSound"), lineCount = 516, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_inlinenoteattachment_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "LinkPreviewCard.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/LinkPreviewCard.kt",
            purpose = "Sistema completo de previews de enlaces. Detecta URLs, recupera/limita HTML, extrae Open Graph/metadatos, maneja plataformas especiales, cachea texto e imágenes y dibuja tarjetas con estados de carga/error.",
            whereInApp = "Editor, detalle y tarjetas de nota cuando el contenido contiene URLs o marcadores de enlaces embebidos.",
            details = "Sistema completo de previews de enlaces. Detecta URLs, recupera/limita HTML, extrae Open Graph/metadatos, maneja plataformas especiales, cachea texto " +
                "e imágenes y dibuja tarjetas con estados de carga/error. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde " +
                "AppSettings y evita modificar almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más " +
                "relevantes están fun extractLinkUrls, fun extractEmbeddedLinkUrls, fun stripEmbeddedLinkMetadata, fun noteContentForStorage, fun noteTextForDisplay, " +
                "class LinkPreviewData. Sus imports internos muestran una conexión directa con com.example.mynotes.ui.theme.resolveUiTextColor, " +
                "com.example.mynotes.ui.theme.resolveSecondaryUiTextColor, com.example.mynotes.ui.theme.resolveUiGraphicColor, " +
                "app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt, com.example.mynotes.ui.sound.UiActionSound. El snapshot incluido corresponde a esta " +
                "versión del proyecto y contiene 1672 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun extractLinkUrls", "fun extractEmbeddedLinkUrls", "fun stripEmbeddedLinkMetadata", "fun noteContentForStorage", "fun noteTextForDisplay", "class LinkPreviewData", "fun basic", "object LinkPreviewRepository"),
            internalDependencies = listOf("com.example.mynotes.ui.theme.resolveUiTextColor", "com.example.mynotes.ui.theme.resolveSecondaryUiTextColor", "com.example.mynotes.ui.theme.resolveUiGraphicColor", "app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt", "com.example.mynotes.ui.sound.UiActionSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt"), lineCount = 1672, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_linkpreviewcard_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "NoteCard.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/NoteCard.kt",
            purpose = "Construye la tarjeta principal de cada nota, incluyendo fondo/contorno, título, preview, adjuntos, favorito, acciones y menú contextual. También adapta su densidad y animaciones a AppSettings.",
            whereInApp = "Pantalla Mis notas, tanto en cuadrícula/lista como en variantes configurables.",
            details = "Construye la tarjeta principal de cada nota, incluyendo fondo/contorno, título, preview, adjuntos, favorito, acciones y menú contextual. También " +
                "adapta su densidad y animaciones a AppSettings. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde AppSettings " +
                "y evita modificar almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más relevantes " +
                "están fun ModernNoteCard, fun NoteCardActionButton, fun NoteCardActionRow, fun finishMainMenu, fun openSubmenu, fun CategoryPill. Sus imports internos " +
                "muestran una conexión directa con com.example.mynotes.R, com.example.mynotes.ui.components.AppDropdownMenu, " +
                "app/src/main/java/com/example/mynotes/data/Attachment.kt, app/src/main/java/com/example/mynotes/data/Note.kt, " +
                "app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt. El snapshot incluido corresponde a esta versión del proyecto y contiene " +
                "746 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun ModernNoteCard", "fun NoteCardActionButton", "fun NoteCardActionRow", "fun finishMainMenu", "fun openSubmenu", "fun CategoryPill", "fun NoteCardAttachmentsPreview", "fun NoteCardAttachmentTile"),
            internalDependencies = listOf("com.example.mynotes.R", "com.example.mynotes.ui.components.AppDropdownMenu", "app/src/main/java/com/example/mynotes/data/Attachment.kt", "app/src/main/java/com/example/mynotes/data/Note.kt", "app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt", "app/src/main/java/com/example/mynotes/settings/MenuPreferencePolicy.kt", "com.example.mynotes.ui.theme.automaticUiTextColor", "com.example.mynotes.ui.theme.resolveUiTextColor"), lineCount = 746, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_notecard_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "NoteCardStyle.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/NoteCardStyle.kt",
            purpose = "Modelo pequeño que traduce AppSettings a decisiones concretas de tarjeta: contorno, número máximo de líneas y visibilidad de favorito. Mantiene esas reglas fuera de NoteCard.",
            whereInApp = "Es consultado al construir cada NoteCard para no repetir cálculos de estilo.",
            details = "Modelo pequeño que traduce AppSettings a decisiones concretas de tarjeta: contorno, número máximo de líneas y visibilidad de favorito. Mantiene esas " +
                "reglas fuera de NoteCard. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde AppSettings y evita modificar " +
                "almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más relevantes están class " +
                "NoteCardStyle, fun AppSettings. Sus imports internos muestran una conexión directa con app/src/main/java/com/example/mynotes/settings/AppSettings.kt. " +
                "El snapshot incluido corresponde a esta versión del proyecto y contiene 16 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta " +
                "visualizarlo completo.",
            keySymbols = listOf("class NoteCardStyle", "fun AppSettings"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/settings/AppSettings.kt"), lineCount = 16, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_notecardstyle_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "OptionsMenuCustomizationSection.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/OptionsMenuCustomizationSection.kt",
            purpose = "UI de Ajustes para personalizar apariencia y contenido del menú de opciones de una nota. Usa descriptores de opciones y catálogos de prioridad/color para editar orden, visibilidad y estilo.",
            whereInApp = "Configuración → personalización del menú de opciones.",
            details = "UI de Ajustes para personalizar apariencia y contenido del menú de opciones de una nota. Usa descriptores de opciones y catálogos de prioridad/color " +
                "para editar orden, visibilidad y estilo. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde AppSettings y " +
                "evita modificar almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más relevantes " +
                "están class MenuOptionDescriptor, fun OptionsMenuHeader, fun OptionsMenuAppearanceSettingsSection, fun OptionsMenuMainActionsSettingsSection, fun " +
                "OptionsMenuPrioritySettingsSection, fun OptionsMenuColorSettingsSection. Sus imports internos muestran una conexión directa con com.example.mynotes.R, " +
                "com.example.mynotes.ui.components.AppDropdownMenu, app/src/main/java/com/example/mynotes/settings/AppSettings.kt, " +
                "app/src/main/java/com/example/mynotes/settings/MenuPreferencePolicy.kt, com.example.mynotes.ui.sound.UiActionSound. El snapshot incluido corresponde a " +
                "esta versión del proyecto y contiene 407 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class MenuOptionDescriptor", "fun OptionsMenuHeader", "fun OptionsMenuAppearanceSettingsSection", "fun OptionsMenuMainActionsSettingsSection", "fun OptionsMenuPrioritySettingsSection", "fun OptionsMenuColorSettingsSection", "fun OptionsMenuResetAction", "fun MenuOrderRow"),
            internalDependencies = listOf("com.example.mynotes.R", "com.example.mynotes.ui.components.AppDropdownMenu", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "app/src/main/java/com/example/mynotes/settings/MenuPreferencePolicy.kt", "com.example.mynotes.ui.sound.UiActionSound", "com.example.mynotes.ui.sound.UiSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt"), lineCount = 407, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_optionsmenucustomizationsection_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "PaletteSelector.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/PaletteSelector.kt",
            purpose = "Selector visual de paletas con tarjetas de muestra. Renderiza filas, estados seleccionados y feedback sin duplicar la definición de colores, que proviene de PaletteCatalog.",
            whereInApp = "Configuración de apariencia cuando se elige una paleta general.",
            details = "Selector visual de paletas con tarjetas de muestra. Renderiza filas, estados seleccionados y feedback sin duplicar la definición de colores, que " +
                "proviene de PaletteCatalog. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde AppSettings y evita modificar " +
                "almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más relevantes están fun " +
                "PaletteSelector, fun PaletteSelectorRow, fun PaletteCard, fun PaletteToneCircle. Sus imports internos muestran una conexión directa con " +
                "app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt, com.example.mynotes.ui.sound.UiActionSound, " +
                "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt, com.example.mynotes.ui.theme.MyNotesPalette, " +
                "com.example.mynotes.ui.theme.resolveUiGraphicColor. El snapshot incluido corresponde a esta versión del proyecto y contiene 264 líneas; puede " +
                "recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun PaletteSelector", "fun PaletteSelectorRow", "fun PaletteCard", "fun PaletteToneCircle"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt", "com.example.mynotes.ui.sound.UiActionSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt", "com.example.mynotes.ui.theme.MyNotesPalette", "com.example.mynotes.ui.theme.resolveUiGraphicColor", "com.example.mynotes.ui.theme.resolveUiTextColor"), lineCount = 264, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_paletteselector_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "PaletteSettingsSegment.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/PaletteSettingsSegment.kt",
            purpose = "Dibuja el segmento visual que representa una paleta dentro de controles de Ajustes, incluida su geometría/curvas. Es un componente puramente visual y reutilizable.",
            whereInApp = "Se usa dentro de selectores/preview de paleta en Ajustes.",
            details = "Dibuja el segmento visual que representa una paleta dentro de controles de Ajustes, incluida su geometría/curvas. Es un componente puramente visual y " +
                "reutilizable. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde AppSettings y evita modificar almacenamiento " +
                "directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más relevantes están fun PaletteSettingsSegment. " +
                "Sus imports internos muestran una conexión directa con app/src/main/java/com/example/mynotes/ui/theme/SettingsSectionColors.kt, " +
                "com.example.mynotes.ui.theme.settingsSectionColors. El snapshot incluido corresponde a esta versión del proyecto y contiene 71 líneas; puede " +
                "recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun PaletteSettingsSegment"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/ui/theme/SettingsSectionColors.kt", "com.example.mynotes.ui.theme.settingsSectionColors"), lineCount = 71, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_palettesettingssegment_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "ProfileImageEditorDialog.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/ProfileImageEditorDialog.kt",
            purpose = "Editor de imagen de perfil con carga orientada por EXIF, recorte cuadrado, zoom/pan, mezcla de colores y guardado del resultado normalizado. Mantiene límites de tamaño para no disparar memoria.",
            whereInApp = "Configuración/perfil al elegir o ajustar la imagen de usuario.",
            details = "Editor de imagen de perfil con carga orientada por EXIF, recorte cuadrado, zoom/pan, mezcla de colores y guardado del resultado normalizado. Mantiene " +
                "límites de tamaño para no disparar memoria. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde AppSettings y " +
                "evita modificar almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más relevantes " +
                "están fun ProfileImageEditorDialog, fun managedProfileSourceUri, fun clearManagedProfileImages, fun clampProfileOffset, fun decodeProfileBitmap, fun " +
                "decodeProfileBitmapLegacy. Sus imports internos muestran una conexión directa con com.example.mynotes.util.exifOrientationMatrix, " +
                "com.example.mynotes.util.openUriStream, com.example.mynotes.R. El snapshot incluido corresponde a esta versión del proyecto y contiene 517 líneas; " +
                "puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun ProfileImageEditorDialog", "fun managedProfileSourceUri", "fun clearManagedProfileImages", "fun clampProfileOffset", "fun decodeProfileBitmap", "fun decodeProfileBitmapLegacy", "fun applyLegacyExifOrientation", "fun saveProfileCrop"),
            internalDependencies = listOf("com.example.mynotes.util.exifOrientationMatrix", "com.example.mynotes.util.openUriStream", "com.example.mynotes.R"), lineCount = 517, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_profileimageeditordialog_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "ScrollPositionCapsule.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/ScrollPositionCapsule.kt",
            purpose = "Indicador vertical de posición para ScrollState, LazyListState y LazyStaggeredGridState. Calcula fracción visible/progreso y garantiza contraste adaptable sin interceptar gestos.",
            whereInApp = "Aparece en pantallas largas como Ajustes, información, listas y cuadrículas para mostrar visualmente dónde está el usuario.",
            details = "Indicador vertical de posición para ScrollState, LazyListState y LazyStaggeredGridState. Calcula fracción visible/progreso y garantiza contraste " +
                "adaptable sin interceptar gestos. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde AppSettings y evita " +
                "modificar almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más relevantes están " +
                "fun ScrollPositionCapsule, fun ScrollStateCapsuleThumb, fun LazyCapsuleThumb, fun adaptiveScrollCapsuleColor, fun CapsuleFrame, fun CapsuleThumb. Sus " +
                "imports internos muestran una conexión directa con com.example.mynotes.ui.theme.softenUiColorToContrast. El snapshot incluido corresponde a esta " +
                "versión del proyecto y contiene 276 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun ScrollPositionCapsule", "fun ScrollStateCapsuleThumb", "fun LazyCapsuleThumb", "fun adaptiveScrollCapsuleColor", "fun CapsuleFrame", "fun CapsuleThumb"),
            internalDependencies = listOf("com.example.mynotes.ui.theme.softenUiColorToContrast"), lineCount = 276, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_scrollpositioncapsule_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "SettingsSectionPanel.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/SettingsSectionPanel.kt",
            purpose = "Componentes base compartidos de Ajustes: paneles, texto secundario y filas de toggle. Centraliza padding, forma, tamaños y alpha para que secciones distintas no diverjan visualmente.",
            whereInApp = "Muchas secciones de SettingsScreen, ExtremeCustomizationSection, OptionsMenuCustomizationSection y BackupRestoreSection.",
            details = "Componentes base compartidos de Ajustes: paneles, texto secundario y filas de toggle. Centraliza padding, forma, tamaños y alpha para que secciones " +
                "distintas no diverjan visualmente. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva colores/tamaños desde AppSettings y evita " +
                "modificar almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. Entre sus símbolos más relevantes están " +
                "fun SettingsSectionPanel, fun SettingsDropdownItem, fun SettingsSecondaryText, fun SettingsToggleRow. Sus imports internos muestran una conexión " +
                "directa con app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt, app/src/main/java/com/example/mynotes/ui/theme/SettingsSectionColors.kt, " +
                "com.example.mynotes.ui.theme.settingsSectionColors. El snapshot incluido corresponde a esta versión del proyecto y contiene 121 líneas; puede " +
                "recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun SettingsSectionPanel", "fun SettingsDropdownItem", "fun SettingsSecondaryText", "fun SettingsToggleRow"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt", "app/src/main/java/com/example/mynotes/ui/theme/SettingsSectionColors.kt", "com.example.mynotes.ui.theme.settingsSectionColors"), lineCount = 121, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_settingssectionpanel_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Componentes de interfaz", title = "StyledSettingsSlider.kt",
            path = "app/src/main/java/com/example/mynotes/ui/components/StyledSettingsSlider.kt",
            purpose = "Implementa los estilos configurables de slider usados en Ajustes, incluyendo variantes visuales y feedback. Traduce el estilo seleccionado a geometría/colores concretos conservando el mismo contrato de valor.",
            whereInApp = "Controles deslizantes de volumen, intensidad, tamaños y personalización dentro de Ajustes.",
            details = "Implementa los estilos configurables de slider usados en Ajustes, incluyendo variantes visuales y feedback. Traduce el estilo seleccionado a " +
                "geometría/colores concretos conservando el mismo contrato de valor. Es un bloque Compose reutilizable: recibe estado y callbacks, deriva " +
                "colores/tamaños desde AppSettings y evita modificar almacenamiento directamente. El estado persistente sigue perteneciendo a ViewModels/repositorios. " +
                "Entre sus símbolos más relevantes están fun StyledSettingsSlider, fun discretePointCount, fun indexFraction, fun androidx. Sus imports internos " +
                "muestran una conexión directa con com.example.mynotes.ui.sound.UiSound, app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt. El snapshot " +
                "incluido corresponde a esta versión del proyecto y contiene 266 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo " +
                "completo.",
            keySymbols = listOf("fun StyledSettingsSlider", "fun discretePointCount", "fun indexFraction", "fun androidx"),
            internalDependencies = listOf("com.example.mynotes.ui.sound.UiSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt"), lineCount = 266, assetName = "src_app_src_main_java_com_example_mynotes_ui_components_styledsettingsslider_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Multimedia", title = "MediaPlaybackState.kt",
            path = "app/src/main/java/com/example/mynotes/ui/media/MediaPlaybackState.kt",
            purpose = "Estado estable y binding reusable para ExoPlayer. Maneja preparado/buffering/play/error/duración/posición, listeners, TextureView, seek slider, progreso periódico y liberación segura.",
            whereInApp = "Adjuntos de audio/vídeo inline y visores que necesitan reproducir multimedia con Compose.",
            details = "Estado estable y binding reusable para ExoPlayer. Maneja preparado/buffering/play/error/duración/posición, listeners, TextureView, seek slider, " +
                "progreso periódico y liberación segura. La reproducción se trata como un recurso con ciclo de vida: se prepara al entrar en composición, se observa " +
                "con listeners y se libera al salir. Esto reduce fugas y evita que varios elementos mantengan decodificadores activos sin necesidad. Entre sus símbolos " +
                "más relevantes están class MediaPlaybackState, fun reset, fun beginBuffering, fun markError, fun fail, fun seek. Sus imports internos muestran una " +
                "conexión directa con com.example.mynotes.ui.sound.UiActionSound, app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt. El snapshot incluido " +
                "corresponde a esta versión del proyecto y contiene 215 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class MediaPlaybackState", "fun reset", "fun beginBuffering", "fun markError", "fun fail", "fun seek", "fun toggle", "fun onPlaybackStateChanged"),
            internalDependencies = listOf("com.example.mynotes.ui.sound.UiActionSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt"), lineCount = 215, assetName = "src_app_src_main_java_com_example_mynotes_ui_media_mediaplaybackstate_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Animaciones", title = "AppMotion.kt",
            path = "app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt",
            purpose = "Motor central de movimiento. Contiene estilos de transición, easings, escalado de duración y wrappers de AnimatedContent/entrada para que toda la app use animaciones coherentes y desactivables.",
            whereInApp = "Navegación entre pantallas, aparición de tarjetas, menús y elementos que respetan las preferencias de animación.",
            details = "Motor central de movimiento. Contiene estilos de transición, easings, escalado de duración y wrappers de AnimatedContent/entrada para que toda la app " +
                "use animaciones coherentes y desactivables. Las transiciones se describen con primitivas Compose y parámetros de AppSettings. Centralizarlas hace que " +
                "velocidad, estilo y desactivación de animaciones se propaguen sin reescribir cada pantalla. Entre sus símbolos más relevantes están object AppMotion, " +
                "fun normalizeStyle, fun normalizeEasing, fun normalizePerformanceMode, fun duration, fun easing. El snapshot incluido corresponde a esta versión del " +
                "proyecto y contiene 339 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object AppMotion", "fun normalizeStyle", "fun normalizeEasing", "fun normalizePerformanceMode", "fun duration", "fun easing", "fun horizontalOffset", "fun verticalOffset"),
            internalDependencies = listOf(), lineCount = 339, assetName = "src_app_src_main_java_com_example_mynotes_ui_motion_appmotion_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "PDF Studio", title = "ImmersivePdfActivity.kt",
            path = "app/src/main/java/com/example/mynotes/ui/pdf/ImmersivePdfActivity.kt",
            purpose = "Actividad ligera para visualizar un PDF en modo inmersivo. Aplica tema/configuración global y delega renderizado/navegación al contenido PDF correspondiente.",
            whereInApp = "Se usa cuando un PDF se abre en la experiencia inmersiva separada del editor principal.",
            details = "Actividad ligera para visualizar un PDF en modo inmersivo. Aplica tema/configuración global y delega renderizado/navegación al contenido PDF " +
                "correspondiente. La arquitectura PDF separa modelos, persistencia, renderizado y edición. Cada pieza intercambia datos explícitos para que " +
                "guardar/exportar/renderizar no dependa de detalles visuales de una toolbar o gesto concreto. Entre sus símbolos más relevantes están class " +
                "ImmersivePdfActivity, fun attachBaseContext, fun onCreate, fun setPdfContent, fun onResume, fun onDestroy. Sus imports internos muestran una conexión " +
                "directa con com.example.mynotes.R, com.example.mynotes.SyncDisplayPerformance, com.example.mynotes.SyncUiFeedback, " +
                "com.example.mynotes.hideAndroidNavigationBar, app/src/main/java/com/example/mynotes/performance/DisplayPerformanceController.kt. El snapshot incluido " +
                "corresponde a esta versión del proyecto y contiene 64 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class ImmersivePdfActivity", "fun attachBaseContext", "fun onCreate", "fun setPdfContent", "fun onResume", "fun onDestroy", "fun onWindowFocusChanged"),
            internalDependencies = listOf("com.example.mynotes.R", "com.example.mynotes.SyncDisplayPerformance", "com.example.mynotes.SyncUiFeedback", "com.example.mynotes.hideAndroidNavigationBar", "app/src/main/java/com/example/mynotes/performance/DisplayPerformanceController.kt", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt", "com.example.mynotes.ui.theme.MyNotesTheme"), lineCount = 64, assetName = "src_app_src_main_java_com_example_mynotes_ui_pdf_immersivepdfactivity_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "PDF Studio", title = "PdfDocumentEngine.kt",
            path = "app/src/main/java/com/example/mynotes/ui/pdf/PdfDocumentEngine.kt",
            purpose = "Motor de carga/render/export de PDF basado en PdfRenderer. Renderiza páginas a Bitmap con límites de resolución, mantiene relación de aspecto y compone páginas editadas al exportar.",
            whereInApp = "PDF Studio y biblioteca PDF cuando se necesitan thumbnails, páginas rasterizadas o un PDF final.",
            details = "Motor de carga/render/export de PDF basado en PdfRenderer. Renderiza páginas a Bitmap con límites de resolución, mantiene relación de aspecto y " +
                "compone páginas editadas al exportar. La arquitectura PDF separa modelos, persistencia, renderizado y edición. Cada pieza intercambia datos explícitos " +
                "para que guardar/exportar/renderizar no dependa de detalles visuales de una toolbar o gesto concreto. Entre sus símbolos más relevantes están object " +
                "PdfDocumentEngine, fun loadPdfLazy, fun renderPdfPage, fun renderPage, fun loadPdf, fun renderProjectThumbnail. El snapshot incluido corresponde a " +
                "esta versión del proyecto y contiene 277 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object PdfDocumentEngine", "fun loadPdfLazy", "fun renderPdfPage", "fun renderPage", "fun loadPdf", "fun renderProjectThumbnail", "fun loadImage", "fun exportPdf"),
            internalDependencies = listOf(), lineCount = 277, assetName = "src_app_src_main_java_com_example_mynotes_ui_pdf_pdfdocumentengine_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "PDF Studio", title = "PdfEditorActivity.kt",
            path = "app/src/main/java/com/example/mynotes/ui/pdf/PdfEditorActivity.kt",
            purpose = "Pantalla y controlador principal de PDF Studio. Mantiene páginas, herramientas, selección, undo/redo, imágenes/textos/trazos, zoom, import/export, Recorte IA y guardado inteligente del proyecto.",
            whereInApp = "Toda la experiencia PDF Studio, incluidos toolbars, interacción directa con elementos y el botón Atrás corregido.",
            details = "Pantalla y controlador principal de PDF Studio. Mantiene páginas, herramientas, selección, undo/redo, imágenes/textos/trazos, zoom, import/export, " +
                "Recorte IA y guardado inteligente del proyecto. La arquitectura PDF separa modelos, persistencia, renderizado y edición. Cada pieza intercambia datos " +
                "explícitos para que guardar/exportar/renderizar no dependa de detalles visuales de una toolbar o gesto concreto. Entre sus símbolos más relevantes " +
                "están class PdfEditorActivity, fun onCreate, fun PdfEditorScreen, fun setSelection, fun clearSelection, fun navigatePage. Sus imports internos " +
                "muestran una conexión directa con com.example.mynotes.R, app/src/main/java/com/example/mynotes/settings/AppSettings.kt, " +
                "app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt, com.example.mynotes.ui.sound.UiActionSound, com.example.mynotes.ui.sound.UiSound. El " +
                "snapshot incluido corresponde a esta versión del proyecto y contiene 2240 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta " +
                "visualizarlo completo.",
            keySymbols = listOf("class PdfEditorActivity", "fun onCreate", "fun PdfEditorScreen", "fun setSelection", "fun clearSelection", "fun navigatePage", "fun markDirty", "fun commit"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt", "com.example.mynotes.ui.sound.UiActionSound", "com.example.mynotes.ui.sound.UiSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt", "com.example.mynotes.ui.theme.appFontFamily", "com.example.mynotes.ui.theme.resolveSecondaryUiTextColor"), lineCount = 2240, assetName = "src_app_src_main_java_com_example_mynotes_ui_pdf_pdfeditoractivity_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "PDF Studio", title = "PdfEditorModels.kt",
            path = "app/src/main/java/com/example/mynotes/ui/pdf/PdfEditorModels.kt",
            purpose = "Modelos de edición independientes de la UI: puntos, trazos, textos, imágenes y páginas. Separan datos serializables/manipulables de la Activity que los dibuja.",
            whereInApp = "PdfEditorActivity, PdfProjectRepository y PdfDocumentEngine intercambian estos modelos.",
            details = "Modelos de edición independientes de la UI: puntos, trazos, textos, imágenes y páginas. Separan datos serializables/manipulables de la Activity que " +
                "los dibuja. La arquitectura PDF separa modelos, persistencia, renderizado y edición. Cada pieza intercambia datos explícitos para que " +
                "guardar/exportar/renderizar no dependa de detalles visuales de una toolbar o gesto concreto. Entre sus símbolos más relevantes están class PdfPoint, " +
                "class PdfStroke, class PdfTextElement, class PdfImageElement, class PdfPageModel, fun blank. El snapshot incluido corresponde a esta versión del " +
                "proyecto y contiene 67 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class PdfPoint", "class PdfStroke", "class PdfTextElement", "class PdfImageElement", "class PdfPageModel", "fun blank", "class PdfRenderedPage", "class PdfEditorTool"),
            internalDependencies = listOf(), lineCount = 67, assetName = "src_app_src_main_java_com_example_mynotes_ui_pdf_pdfeditormodels_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "PDF Studio", title = "PdfLibraryActivity.kt",
            path = "app/src/main/java/com/example/mynotes/ui/pdf/PdfLibraryActivity.kt",
            purpose = "Biblioteca visual de proyectos PDF. Carga resúmenes/thumbnails, permite abrir/crear/eliminar proyectos y coordina navegación hacia el editor o documentos importados.",
            whereInApp = "Pantalla Biblioteca PDF accesible desde el flujo de PDF de MyNotes.",
            details = "Biblioteca visual de proyectos PDF. Carga resúmenes/thumbnails, permite abrir/crear/eliminar proyectos y coordina navegación hacia el editor o " +
                "documentos importados. La arquitectura PDF separa modelos, persistencia, renderizado y edición. Cada pieza intercambia datos explícitos para que " +
                "guardar/exportar/renderizar no dependa de detalles visuales de una toolbar o gesto concreto. Entre sus símbolos más relevantes están class " +
                "PdfLibraryActivity, fun onCreate, fun onResume, fun PdfLibraryScreen, fun PdfInfoChip, fun PdfProjectThumbnail. Sus imports internos muestran una " +
                "conexión directa con com.example.mynotes.R, app/src/main/java/com/example/mynotes/settings/AppSettings.kt, " +
                "com.example.mynotes.ui.components.AppDropdownMenu, com.example.mynotes.ui.sound.UiActionSound, com.example.mynotes.ui.sound.UiSound. El snapshot " +
                "incluido corresponde a esta versión del proyecto y contiene 706 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo " +
                "completo.",
            keySymbols = listOf("class PdfLibraryActivity", "fun onCreate", "fun onResume", "fun PdfLibraryScreen", "fun PdfInfoChip", "fun PdfProjectThumbnail", "fun PdfOptionMenuItem"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "com.example.mynotes.ui.components.AppDropdownMenu", "com.example.mynotes.ui.sound.UiActionSound", "com.example.mynotes.ui.sound.UiSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt", "com.example.mynotes.ui.theme.appFontFamily", "com.example.mynotes.ui.theme.compositeUiColor"), lineCount = 706, assetName = "src_app_src_main_java_com_example_mynotes_ui_pdf_pdflibraryactivity_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "PDF Studio", title = "PdfProjectRepository.kt",
            path = "app/src/main/java/com/example/mynotes/ui/pdf/PdfProjectRepository.kt",
            purpose = "Persistencia de proyectos editables de PDF. Guarda JSON, PDF fuente, bitmaps/miniaturas y export metadata; usa mutex/cachés para que las escrituras sean consistentes y evita reconstrucciones innecesarias.",
            whereInApp = "PdfEditorActivity guarda/recupera aquí sus sesiones y PdfLibraryActivity obtiene sus resúmenes.",
            details = "Persistencia de proyectos editables de PDF. Guarda JSON, PDF fuente, bitmaps/miniaturas y export metadata; usa mutex/cachés para que las escrituras " +
                "sean consistentes y evita reconstrucciones innecesarias. La arquitectura PDF separa modelos, persistencia, renderizado y edición. Cada pieza " +
                "intercambia datos explícitos para que guardar/exportar/renderizar no dependa de detalles visuales de una toolbar o gesto concreto. Entre sus símbolos " +
                "más relevantes están object PdfProjectRepository, class Summary, class Project, fun newProjectId, fun root, fun directory. El snapshot incluido " +
                "corresponde a esta versión del proyecto y contiene 457 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object PdfProjectRepository", "class Summary", "class Project", "fun newProjectId", "fun root", "fun directory", "fun sourceFile", "fun internalSourceUri"),
            internalDependencies = listOf(), lineCount = 457, assetName = "src_app_src_main_java_com_example_mynotes_ui_pdf_pdfprojectrepository_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "PDF Studio", title = "PdfSubjectCropper.kt",
            path = "app/src/main/java/com/example/mynotes/ui/pdf/PdfSubjectCropper.kt",
            purpose = "Implementación del Recorte IA. Prepara frames de inferencia, ejecuta Subject Segmentation con reintentos, usa confidence mask para conservar RGB originales, suaviza alfa y mantiene geometría del elemento.",
            whereInApp = "PDF Studio → seleccionar una imagen → Recorte IA.",
            details = "Implementación del Recorte IA. Prepara frames de inferencia, ejecuta Subject Segmentation con reintentos, usa confidence mask para conservar RGB " +
                "originales, suaviza alfa y mantiene geometría del elemento. La arquitectura PDF separa modelos, persistencia, renderizado y edición. Cada pieza " +
                "intercambia datos explícitos para que guardar/exportar/renderizar no dependa de detalles visuales de una toolbar o gesto concreto. Entre sus símbolos " +
                "más relevantes están object PdfSubjectCropper, fun isolateAndCrop, fun segmentPrecise, fun segmentBySubjects, fun processWithModelRetries, fun " +
                "process. El snapshot incluido corresponde a esta versión del proyecto y contiene 330 líneas; puede recorrerse dentro de la app en bloques de 160 " +
                "líneas hasta visualizarlo completo.",
            keySymbols = listOf("object PdfSubjectCropper", "fun isolateAndCrop", "fun segmentPrecise", "fun segmentBySubjects", "fun processWithModelRetries", "fun process", "fun prepareForInference", "fun prepareSquareRetry"),
            internalDependencies = listOf(), lineCount = 330, assetName = "src_app_src_main_java_com_example_mynotes_ui_pdf_pdfsubjectcropper_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "PDF Studio", title = "PdfSubjectModel.kt",
            path = "app/src/main/java/com/example/mynotes/ui/pdf/PdfSubjectModel.kt",
            purpose = "Gestiona disponibilidad de Google Play Services y del módulo opcional subject_segment. Comprueba red sólo cuando hace falta descargar, instala el módulo y espera a que ML Kit lo vea de forma estable.",
            whereInApp = "Es llamado por PdfSubjectCropper en el primer uso o cuando el módulo de segmentación aún no está listo.",
            details = "Gestiona disponibilidad de Google Play Services y del módulo opcional subject_segment. Comprueba red sólo cuando hace falta descargar, instala el " +
                "módulo y espera a que ML Kit lo vea de forma estable. La arquitectura PDF separa modelos, persistencia, renderizado y edición. Cada pieza intercambia " +
                "datos explícitos para que guardar/exportar/renderizar no dependa de detalles visuales de una toolbar o gesto concreto. Entre sus símbolos más " +
                "relevantes están object PdfSubjectModel, class PlayServicesUnavailableException, class ModelUnavailableException, class NoInternetConnectionException, " +
                "fun isTemporarilyUnavailable, fun prepare. El snapshot incluido corresponde a esta versión del proyecto y contiene 150 líneas; puede recorrerse dentro " +
                "de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object PdfSubjectModel", "class PlayServicesUnavailableException", "class ModelUnavailableException", "class NoInternetConnectionException", "fun isTemporarilyUnavailable", "fun prepare", "fun Context", "fun awaitStableAvailability"),
            internalDependencies = listOf(), lineCount = 150, assetName = "src_app_src_main_java_com_example_mynotes_ui_pdf_pdfsubjectmodel_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Sonido y hápticos", title = "UiHapticPlayer.kt",
            path = "app/src/main/java/com/example/mynotes/ui/sound/UiHapticPlayer.kt",
            purpose = "Motor global de vibración. Normaliza estilos, escala intensidades, aplica throttling y elige APIs de vibración compatibles según versión de Android.",
            whereInApp = "Botones y acciones de toda la app cuando los efectos hápticos están habilitados.",
            details = "Motor global de vibración. Normaliza estilos, escala intensidades, aplica throttling y elige APIs de vibración compatibles según versión de Android. " +
                "El feedback se recibe como una acción semántica y el motor decide recurso, volumen, intensidad y throttling. De esta manera un botón no necesita " +
                "conocer SoundPool/Vibrator ni duplicar preferencias. Entre sus símbolos más relevantes están class UiHaptic, object UiHapticPlayer, fun " +
                "normalizeStyle, fun configure, fun play, fun playThrottled. Sus imports internos muestran una conexión directa con " +
                "app/src/main/java/com/example/mynotes/settings/FeedbackPreferencePolicy.kt. El snapshot incluido corresponde a esta versión del proyecto y contiene " +
                "183 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class UiHaptic", "object UiHapticPlayer", "fun normalizeStyle", "fun configure", "fun play", "fun playThrottled", "fun playToggle", "fun previewStyle"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/settings/FeedbackPreferencePolicy.kt"), lineCount = 183, assetName = "src_app_src_main_java_com_example_mynotes_ui_sound_uihapticplayer_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Sonido y hápticos", title = "UiSoundPlayer.kt",
            path = "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt",
            purpose = "Motor global de sonidos de interfaz basado en SoundPool y políticas de feedback. Mapea acciones semánticas a sonidos, aplica volumen/tema/throttling y coordina hápticos cuando corresponde.",
            whereInApp = "Navegación, toggles, menús, creación, guardado, PDF y prácticamente cualquier interacción con feedback.",
            details = "Motor global de sonidos de interfaz basado en SoundPool y políticas de feedback. Mapea acciones semánticas a sonidos, aplica volumen/tema/throttling y " +
                "coordina hápticos cuando corresponde. El feedback se recibe como una acción semántica y el motor decide recurso, volumen, intensidad y throttling. De " +
                "esta manera un botón no necesita conocer SoundPool/Vibrator ni duplicar preferencias. Entre sus símbolos más relevantes están class UiSound, class " +
                "UiActionSound, object UiSoundPlayer, fun normalizeTheme, fun configure, fun play. Sus imports internos muestran una conexión directa con " +
                "app/src/main/java/com/example/mynotes/settings/FeedbackPreferencePolicy.kt. El snapshot incluido corresponde a esta versión del proyecto y contiene " +
                "275 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class UiSound", "class UiActionSound", "object UiSoundPlayer", "fun normalizeTheme", "fun configure", "fun play", "fun playAction", "fun runAction"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/settings/FeedbackPreferencePolicy.kt"), lineCount = 275, assetName = "src_app_src_main_java_com_example_mynotes_ui_sound_uisoundplayer_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "AppFonts.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/AppFonts.kt",
            purpose = "Resuelve la familia tipográfica seleccionada, incluida Google Sans descargable y alternativas del sistema. Encapsula el proveedor de Google Fonts para no repetirlo en cada pantalla.",
            whereInApp = "Todas las pantallas que llaman appFontFamily(settings.font).",
            details = "Resuelve la familia tipográfica seleccionada, incluida Google Sans descargable y alternativas del sistema. Encapsula el proveedor de Google Fonts para " +
                "no repetirlo en cada pantalla. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite callbacks. La " +
                "personalización visual se mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más relevantes están fun " +
                "appFontFamily. Sus imports internos muestran una conexión directa con com.example.mynotes.R, " +
                "app/src/main/java/com/example/mynotes/settings/FontPreferencePolicy.kt. El snapshot incluido corresponde a esta versión del proyecto y contiene 41 " +
                "líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun appFontFamily"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/settings/FontPreferencePolicy.kt"), lineCount = 41, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_appfonts_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "Color.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/Color.kt",
            purpose = "Paleta base generada por la plantilla Compose. Conserva colores Material de referencia que pueden actuar como fallback o soporte del tema.",
            whereInApp = "Infraestructura de tema; la personalización avanzada usa además PaletteCatalog/Theme.",
            details = "Paleta base generada por la plantilla Compose. Conserva colores Material de referencia que pueden actuar como fallback o soporte del tema. La " +
                "pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite callbacks. La personalización visual se mantiene separada " +
                "de Room/DataStore para que recomposición no implique I/O. El snapshot incluido corresponde a esta versión del proyecto y contiene 11 líneas; puede " +
                "recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf(),
            internalDependencies = listOf(), lineCount = 11, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_color_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "DevelopmentCodeCatalog.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/DevelopmentCodeCatalog.kt",
            purpose = "Catálogo generado que describe todos los archivos documentados: categoría, ruta, uso, explicación, símbolos, dependencias, número de líneas y asset que contiene la fuente real.",
            whereInApp = "Código de desarrollo lo consulta para construir la lista y enlazar cada tarjeta con su snapshot de código.",
            details = "Catálogo generado que describe todos los archivos documentados: categoría, ruta, uso, explicación, símbolos, dependencias, número de líneas y asset " +
                "que contiene la fuente real. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite callbacks. La " +
                "personalización visual se mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más relevantes están class " +
                "DevelopmentFileDoc, object DevelopmentCodeCatalog. El snapshot incluido corresponde a esta versión del proyecto y contiene 1362 líneas; puede " +
                "recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class DevelopmentFileDoc", "object DevelopmentCodeCatalog"),
            internalDependencies = listOf(), lineCount = 1362, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_developmentcodecatalog_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "DevelopmentCodeScreen.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/DevelopmentCodeScreen.kt",
            purpose = "Pantalla de documentación interna del código. Presenta el catálogo completo, permite buscar archivos, expandir explicaciones y cargar la fuente real en bloques grandes sin componer miles de líneas de golpe.",
            whereInApp = "Mis notas → botón + → Código de desarrollo.",
            details = "Pantalla de documentación interna del código. Presenta el catálogo completo, permite buscar archivos, expandir explicaciones y cargar la fuente real " +
                "en bloques grandes sin componer miles de líneas de golpe. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y " +
                "emite callbacks. La personalización visual se mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más " +
                "relevantes están fun DevelopmentCodeScreen, fun DevelopmentCodeIntro, fun DevelopmentFileCard, fun DevelopmentFileDetails, fun Context, fun " +
                "DevelopmentDetailLabel. Sus imports internos muestran una conexión directa con app/src/main/java/com/example/mynotes/settings/AppSettings.kt, " +
                "app/src/main/java/com/example/mynotes/ui/components/ScrollPositionCapsule.kt, com.example.mynotes.ui.motion.AnimatedScreenEntry, " +
                "com.example.mynotes.ui.theme.appFontFamily, com.example.mynotes.ui.theme.ensureUiContrast. El snapshot incluido corresponde a esta versión del " +
                "proyecto y contiene 306 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun DevelopmentCodeScreen", "fun DevelopmentCodeIntro", "fun DevelopmentFileCard", "fun DevelopmentFileDetails", "fun Context", "fun DevelopmentDetailLabel", "fun DevelopmentCodeBlock"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "app/src/main/java/com/example/mynotes/ui/components/ScrollPositionCapsule.kt", "com.example.mynotes.ui.motion.AnimatedScreenEntry", "com.example.mynotes.ui.theme.appFontFamily", "com.example.mynotes.ui.theme.ensureUiContrast", "com.example.mynotes.ui.theme.resolveSecondaryUiTextColor", "com.example.mynotes.ui.theme.resolveUiTextColor"), lineCount = 306, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_developmentcodescreen_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "DevelopmentInfoScreen.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/DevelopmentInfoScreen.kt",
            purpose = "Pantalla informativa sobre versión, SDK, arquitectura, almacenamiento, rendimiento, licencias y repositorio. También contiene los componentes de layout reutilizados por pantallas informativas.",
            whereInApp = "Información de desarrollo desde la navegación de la app.",
            details = "Pantalla informativa sobre versión, SDK, arquitectura, almacenamiento, rendimiento, licencias y repositorio. También contiene los componentes de " +
                "layout reutilizados por pantallas informativas. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite " +
                "callbacks. La personalización visual se mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más relevantes " +
                "están fun Context, fun DevelopmentInfoScreen, fun DevelopmentTopBar, fun InformationScreenLayout, fun DevelopmentSection, fun DevelopmentValueRow. Sus " +
                "imports internos muestran una conexión directa con com.example.mynotes.R, app/src/main/java/com/example/mynotes/settings/AppSettings.kt, " +
                "app/src/main/java/com/example/mynotes/settings/DeveloperFeatures.kt, app/src/main/java/com/example/mynotes/ui/components/ScrollPositionCapsule.kt, " +
                "com.example.mynotes.ui.motion.AnimatedScreenEntry. El snapshot incluido corresponde a esta versión del proyecto y contiene 352 líneas; puede " +
                "recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun Context", "fun DevelopmentInfoScreen", "fun DevelopmentTopBar", "fun InformationScreenLayout", "fun DevelopmentSection", "fun DevelopmentValueRow", "fun DevelopmentParagraph", "fun PerformanceProfileRow"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "app/src/main/java/com/example/mynotes/settings/DeveloperFeatures.kt", "app/src/main/java/com/example/mynotes/ui/components/ScrollPositionCapsule.kt", "com.example.mynotes.ui.motion.AnimatedScreenEntry", "com.example.mynotes.ui.sound.UiActionSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt", "com.example.mynotes.ui.theme.appFontFamily"), lineCount = 352, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_developmentinfoscreen_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "DrawingScreen.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/DrawingScreen.kt",
            purpose = "Lienzo de dibujo Compose con herramientas, colores, tamaños, undo/redo/limpiar y expansión del canvas. Mantiene los trazos como datos y los convierte en imagen cuando debe adjuntarse/guardarse.",
            whereInApp = "Dibujo accesible desde el botón + o flujos de creación de nota.",
            details = "Lienzo de dibujo Compose con herramientas, colores, tamaños, undo/redo/limpiar y expansión del canvas. Mantiene los trazos como datos y los convierte " +
                "en imagen cuando debe adjuntarse/guardarse. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite callbacks. " +
                "La personalización visual se mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más relevantes están class " +
                "DrawingStrokeData, class DrawingTool, fun DrawingScreen, fun currentStroke, fun drawStrokeData, fun DrawingHistoryButton. Sus imports internos " +
                "muestran una conexión directa con com.example.mynotes.R, app/src/main/java/com/example/mynotes/settings/AppSettings.kt, " +
                "com.example.mynotes.ui.sound.UiActionSound, app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt, " +
                "com.example.mynotes.ui.theme.appFontFamily. El snapshot incluido corresponde a esta versión del proyecto y contiene 730 líneas; puede recorrerse " +
                "dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class DrawingStrokeData", "class DrawingTool", "fun DrawingScreen", "fun currentStroke", "fun drawStrokeData", "fun DrawingHistoryButton", "fun DrawingColorRow", "fun DrawingColorSwatch"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "com.example.mynotes.ui.sound.UiActionSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt", "com.example.mynotes.ui.theme.appFontFamily"), lineCount = 730, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_drawingscreen_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "NoteColors.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/NoteColors.kt",
            purpose = "Funciones de color/contraste para notas y controles. Calcula compositing, ratio de contraste, texto legible, suavizado y colores adaptativos para botones/superficies.",
            whereInApp = "Tarjetas, detalle, Ajustes, PDF y componentes que deben respetar el color personalizado sin perder legibilidad.",
            details = "Funciones de color/contraste para notas y controles. Calcula compositing, ratio de contraste, texto legible, suavizado y colores adaptativos para " +
                "botones/superficies. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite callbacks. La personalización " +
                "visual se mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más relevantes están fun noteBackgroundColor, " +
                "fun compositeUiColor, fun uiContrastRatio, fun automaticUiTextColor, fun resolveUiTextColor, fun mixOpaqueUiColor. El snapshot incluido corresponde a " +
                "esta versión del proyecto y contiene 316 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun noteBackgroundColor", "fun compositeUiColor", "fun uiContrastRatio", "fun automaticUiTextColor", "fun resolveUiTextColor", "fun mixOpaqueUiColor", "fun softenUiColorToContrast", "fun resolveSecondaryUiTextColor"),
            internalDependencies = listOf(), lineCount = 316, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_notecolors_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "NoteDetailScreen.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/NoteDetailScreen.kt",
            purpose = "Vista de lectura/detalle de una nota. Observa adjuntos, resuelve enlaces/medios, dibuja metadatos y menú de acciones, y ofrece callbacks para editar/favorito/prioridad/categoría/color.",
            whereInApp = "Se abre al tocar una NoteCard.",
            details = "Vista de lectura/detalle de una nota. Observa adjuntos, resuelve enlaces/medios, dibuja metadatos y menú de acciones, y ofrece callbacks para " +
                "editar/favorito/prioridad/categoría/color. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite callbacks. " +
                "La personalización visual se mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más relevantes están fun " +
                "NoteDetailScreen, fun PriorityPill, fun DetailBottomAction, fun shareNote. Sus imports internos muestran una conexión directa con " +
                "com.example.mynotes.R, com.example.mynotes.ui.components.AppAlertDialog, app/src/main/java/com/example/mynotes/ui/components/ScrollPositionCapsule.kt, " +
                "com.example.mynotes.ui.components.AppDropdownMenu, app/src/main/java/com/example/mynotes/data/Note.kt. El snapshot incluido corresponde a esta versión " +
                "del proyecto y contiene 599 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun NoteDetailScreen", "fun PriorityPill", "fun DetailBottomAction", "fun shareNote"),
            internalDependencies = listOf("com.example.mynotes.R", "com.example.mynotes.ui.components.AppAlertDialog", "app/src/main/java/com/example/mynotes/ui/components/ScrollPositionCapsule.kt", "com.example.mynotes.ui.components.AppDropdownMenu", "app/src/main/java/com/example/mynotes/data/Note.kt", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "app/src/main/java/com/example/mynotes/settings/MenuPreferencePolicy.kt", "app/src/main/java/com/example/mynotes/ui/components/InlineNoteAttachment.kt"), lineCount = 599, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_notedetailscreen_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "NoteEditorScreen.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/NoteEditorScreen.kt",
            purpose = "Editor completo de nota. Gestiona título/contenido, adjuntos pendientes, enlaces embebidos, audio/archivos, teclado y guardado; transforma el estado local en parámetros de NoteViewModel.",
            whereInApp = "Nueva nota y edición de una nota existente.",
            details = "Editor completo de nota. Gestiona título/contenido, adjuntos pendientes, enlaces embebidos, audio/archivos, teclado y guardado; transforma el estado " +
                "local en parámetros de NoteViewModel. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite callbacks. La " +
                "personalización visual se mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más relevantes están class " +
                "AttachmentAction, fun removeProcessedUrl, fun shifted, fun NoteEditorScreen, fun startRecording, fun stopRecording. Sus imports internos muestran una " +
                "conexión directa con com.example.mynotes.R, app/src/main/java/com/example/mynotes/data/Attachment.kt, " +
                "app/src/main/java/com/example/mynotes/data/PendingAttachment.kt, app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt, " +
                "app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt. El snapshot incluido corresponde a esta versión del proyecto y contiene 909 líneas; " +
                "puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class AttachmentAction", "fun removeProcessedUrl", "fun shifted", "fun NoteEditorScreen", "fun startRecording", "fun stopRecording", "fun requestVoiceRecording", "fun addPickedAttachments"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/data/Attachment.kt", "app/src/main/java/com/example/mynotes/data/PendingAttachment.kt", "app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt", "app/src/main/java/com/example/mynotes/ui/motion/AppMotion.kt", "app/src/main/java/com/example/mynotes/ui/components/LinkPreviewCard.kt", "app/src/main/java/com/example/mynotes/ui/components/ScrollPositionCapsule.kt", "com.example.mynotes.ui.components.extractLinkUrls"), lineCount = 909, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_noteeditorscreen_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "NotesScreen.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/NotesScreen.kt",
            purpose = "Pantalla principal de Mis notas. Combina notas y adjuntos, aplica filtros/orden, dibuja cuadrícula/lista, encabezados y el menú + de acciones rápidas, incluido Código de desarrollo.",
            whereInApp = "Pantalla inicial de la aplicación.",
            details = "Pantalla principal de Mis notas. Combina notas y adjuntos, aplica filtros/orden, dibuja cuadrícula/lista, encabezados y el menú + de acciones rápidas, " +
                "incluido Código de desarrollo. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite callbacks. La " +
                "personalización visual se mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más relevantes están class " +
                "NoteFilter, class NoteFilterOption, class AttachmentIndex, class QuickCreateSpec, fun noteFilterFromWidgetKey, fun NotesScreen. Sus imports internos " +
                "muestran una conexión directa con com.example.mynotes.R, app/src/main/java/com/example/mynotes/data/Attachment.kt, " +
                "app/src/main/java/com/example/mynotes/data/Note.kt, app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt, " +
                "app/src/main/java/com/example/mynotes/settings/AppSettings.kt. El snapshot incluido corresponde a esta versión del proyecto y contiene 813 líneas; " +
                "puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class NoteFilter", "class NoteFilterOption", "class AttachmentIndex", "class QuickCreateSpec", "fun noteFilterFromWidgetKey", "fun NotesScreen", "fun QuickCreateActionButton", "fun EmptyNotesState"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/data/Attachment.kt", "app/src/main/java/com/example/mynotes/data/Note.kt", "app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "com.example.mynotes.ui.components.ModernNoteCard", "app/src/main/java/com/example/mynotes/ui/components/ScrollPositionCapsule.kt", "com.example.mynotes.ui.components.extractLinkUrls"), lineCount = 813, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_notesscreen_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "PaletteCatalog.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/PaletteCatalog.kt",
            purpose = "Catálogo inmutable de paletas con claves estables y tonos de fondo/superficie/texto/acento. Permite buscar una paleta por clave sin duplicar colores en la UI.",
            whereInApp = "Theme, PaletteSelector y Ajustes consultan este catálogo.",
            details = "Catálogo inmutable de paletas con claves estables y tonos de fondo/superficie/texto/acento. Permite buscar una paleta por clave sin duplicar colores " +
                "en la UI. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite callbacks. La personalización visual se " +
                "mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más relevantes están class MyNotesPalette, object " +
                "PaletteCatalog, fun p, fun find. Sus imports internos muestran una conexión directa con com.example.mynotes.R. El snapshot incluido corresponde a esta " +
                "versión del proyecto y contiene 71 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class MyNotesPalette", "object PaletteCatalog", "fun p", "fun find"),
            internalDependencies = listOf("com.example.mynotes.R"), lineCount = 71, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_palettecatalog_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "ReminderScreen.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/ReminderScreen.kt",
            purpose = "Pantalla de lista y edición de recordatorios. Ofrece tiempos rápidos, repetición, prioridad, colores, validación y callbacks hacia ReminderRepository.",
            whereInApp = "Se abre desde el botón + → Recordatorios.",
            details = "Pantalla de lista y edición de recordatorios. Ofrece tiempos rápidos, repetición, prioridad, colores, validación y callbacks hacia ReminderRepository. " +
                "La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite callbacks. La personalización visual se mantiene " +
                "separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más relevantes están class ReminderColorOption, fun " +
                "reminderColor, fun reminderLocale, fun ReminderScreen, fun ReminderList, fun ReminderCard. Sus imports internos muestran una conexión directa con " +
                "com.example.mynotes.R, app/src/main/java/com/example/mynotes/reminders/Reminder.kt, " +
                "app/src/main/java/com/example/mynotes/reminders/ReminderRepository.kt, app/src/main/java/com/example/mynotes/settings/AppSettings.kt, " +
                "com.example.mynotes.ui.motion.ConfigurableAnimatedContent. El snapshot incluido corresponde a esta versión del proyecto y contiene 827 líneas; puede " +
                "recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class ReminderColorOption", "fun reminderColor", "fun reminderLocale", "fun ReminderScreen", "fun ReminderList", "fun ReminderCard", "fun ReminderEditor", "fun saveReminder"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/reminders/Reminder.kt", "app/src/main/java/com/example/mynotes/reminders/ReminderRepository.kt", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "com.example.mynotes.ui.motion.ConfigurableAnimatedContent", "com.example.mynotes.ui.sound.UiActionSound", "com.example.mynotes.ui.sound.UiSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt"), lineCount = 827, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_reminderscreen_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "SettingsScreen.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/SettingsScreen.kt",
            purpose = "Pantalla extensa de configuración. Ensambla secciones de apariencia, tipografía, animación, rendimiento, sonidos, hápticos, menú, PDF, backups y opciones avanzadas; delega cambios al SettingsViewModel.",
            whereInApp = "Configuración principal de MyNotes.",
            details = "Pantalla extensa de configuración. Ensambla secciones de apariencia, tipografía, animación, rendimiento, sonidos, hápticos, menú, PDF, backups y " +
                "opciones avanzadas; delega cambios al SettingsViewModel. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y " +
                "emite callbacks. La personalización visual se mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más " +
                "relevantes están class SliderStyleOption, fun SettingsScreen, fun rememberSyncedFloatState, fun SettingsFeedbackHeader, fun FeedbackSettingsPanel, fun " +
                "UpdateActionContent. Sus imports internos muestran una conexión directa con com.example.mynotes.R, " +
                "app/src/main/java/com/example/mynotes/reminders/ReminderFeedbackPreferences.kt, app/src/main/java/com/example/mynotes/settings/AppSettings.kt, " +
                "app/src/main/java/com/example/mynotes/settings/DeveloperFeatures.kt, app/src/main/java/com/example/mynotes/settings/FeedbackPreferencePolicy.kt. El " +
                "snapshot incluido corresponde a esta versión del proyecto y contiene 1543 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta " +
                "visualizarlo completo.",
            keySymbols = listOf("class SliderStyleOption", "fun SettingsScreen", "fun rememberSyncedFloatState", "fun SettingsFeedbackHeader", "fun FeedbackSettingsPanel", "fun UpdateActionContent", "fun SettingsToggleRow", "fun SettingsContentSurface"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/reminders/ReminderFeedbackPreferences.kt", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "app/src/main/java/com/example/mynotes/settings/DeveloperFeatures.kt", "app/src/main/java/com/example/mynotes/settings/FeedbackPreferencePolicy.kt", "app/src/main/java/com/example/mynotes/settings/FontPreferencePolicy.kt", "com.example.mynotes.ui.components.AppDropdownMenu", "app/src/main/java/com/example/mynotes/ui/components/BackupRestoreSection.kt"), lineCount = 1543, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_settingsscreen_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "SettingsSectionColors.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/SettingsSectionColors.kt",
            purpose = "Modelo/helper para resolver colores de una sección de Ajustes a partir del tema y contraste. Reduce cálculos repetidos y mantiene consistencia entre paneles.",
            whereInApp = "SettingsScreen y componentes de ajustes que necesitan fondo/texto/borde coherentes.",
            details = "Modelo/helper para resolver colores de una sección de Ajustes a partir del tema y contraste. Reduce cálculos repetidos y mantiene consistencia entre " +
                "paneles. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite callbacks. La personalización visual se " +
                "mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más relevantes están class SettingsSectionColors, fun " +
                "settingsSectionColors. El snapshot incluido corresponde a esta versión del proyecto y contiene 17 líneas; puede recorrerse dentro de la app en bloques " +
                "de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class SettingsSectionColors", "fun settingsSectionColors"),
            internalDependencies = listOf(), lineCount = 17, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_settingssectioncolors_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "SliderColors.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/SliderColors.kt",
            purpose = "Catálogo de opciones de color para sliders de Ajustes. Asocia clave persistente con Color para que StyledSettingsSlider pueda renderizar la elección del usuario.",
            whereInApp = "Configuración de apariencia de sliders.",
            details = "Catálogo de opciones de color para sliders de Ajustes. Asocia clave persistente con Color para que StyledSettingsSlider pueda renderizar la elección " +
                "del usuario. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite callbacks. La personalización visual se " +
                "mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más relevantes están class SliderColorOption. El " +
                "snapshot incluido corresponde a esta versión del proyecto y contiene 48 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta " +
                "visualizarlo completo.",
            keySymbols = listOf("class SliderColorOption"),
            internalDependencies = listOf(), lineCount = 48, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_slidercolors_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "SourceCodeInfoScreen.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/SourceCodeInfoScreen.kt",
            purpose = "Mapa resumido del repositorio y rutas principales. Sirve como índice rápido; Código de desarrollo complementa esta pantalla con explicación y fuente completa de cada archivo.",
            whereInApp = "Información de desarrollo → Código fuente.",
            details = "Mapa resumido del repositorio y rutas principales. Sirve como índice rápido; Código de desarrollo complementa esta pantalla con explicación y fuente " +
                "completa de cada archivo. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite callbacks. La personalización " +
                "visual se mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más relevantes están fun " +
                "SourceCodeInfoScreen, fun SourceSection, fun SourceCodeRow. Sus imports internos muestran una conexión directa con com.example.mynotes.R, " +
                "app/src/main/java/com/example/mynotes/settings/AppSettings.kt, com.example.mynotes.ui.sound.UiActionSound, " +
                "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt, com.example.mynotes.ui.theme.appFontFamily. El snapshot incluido corresponde a esta " +
                "versión del proyecto y contiene 134 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun SourceCodeInfoScreen", "fun SourceSection", "fun SourceCodeRow"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "com.example.mynotes.ui.sound.UiActionSound", "app/src/main/java/com/example/mynotes/ui/sound/UiSoundPlayer.kt", "com.example.mynotes.ui.theme.appFontFamily", "com.example.mynotes.ui.theme.resolveSecondaryUiTextColor", "com.example.mynotes.ui.theme.resolveUiTextColor"), lineCount = 134, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_sourcecodeinfoscreen_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "Theme.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/Theme.kt",
            purpose = "Construye el MaterialTheme final desde AppSettings y PaletteCatalog. Mezcla intensidades, resuelve dark/light, contraste y colores de superficies para que toda la app reaccione a la personalización.",
            whereInApp = "Envuelve el contenido principal de Activities/Compose y afecta visualmente a todas las pantallas.",
            details = "Construye el MaterialTheme final desde AppSettings y PaletteCatalog. Mezcla intensidades, resuelve dark/light, contraste y colores de superficies para " +
                "que toda la app reaccione a la personalización. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite " +
                "callbacks. La personalización visual se mantiene separada de Room/DataStore para que recomposición no implique I/O. Entre sus símbolos más relevantes " +
                "están fun mixColor, fun intensityAmount, fun applySectionIntensity, fun readableContentColor, fun AppSettings, fun resolvedPaletteTextColor. Sus " +
                "imports internos muestran una conexión directa con app/src/main/java/com/example/mynotes/settings/AppSettings.kt. El snapshot incluido corresponde a " +
                "esta versión del proyecto y contiene 318 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun mixColor", "fun intensityAmount", "fun applySectionIntensity", "fun readableContentColor", "fun AppSettings", "fun resolvedPaletteTextColor", "fun resolvedPaletteSecondaryTextColor", "fun resolveAccentColor"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/settings/AppSettings.kt"), lineCount = 318, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_theme_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Pantallas y tema", title = "Type.kt",
            path = "app/src/main/java/com/example/mynotes/ui/theme/Type.kt",
            purpose = "Define la Typography base de Material 3 usada cuando no se sustituye por una familia personalizada. Mantiene un punto único para estilos tipográficos por defecto.",
            whereInApp = "Infraestructura de MaterialTheme.",
            details = "Define la Typography base de Material 3 usada cuando no se sustituye por una familia personalizada. Mantiene un punto único para estilos tipográficos " +
                "por defecto. La pantalla/componente observa estado inmutable, deriva presentación desde AppSettings y emite callbacks. La personalización visual se " +
                "mantiene separada de Room/DataStore para que recomposición no implique I/O. El snapshot incluido corresponde a esta versión del proyecto y contiene 28 " +
                "líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf(),
            internalDependencies = listOf(), lineCount = 28, assetName = "src_app_src_main_java_com_example_mynotes_ui_theme_type_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Actualizaciones", title = "GitHubUpdateManager.kt",
            path = "app/src/main/java/com/example/mynotes/update/GitHubUpdateManager.kt",
            purpose = "Consulta GitHub Releases, interpreta la versión más reciente, cachea resultados/descargas y prepara el APK de actualización para entregarlo al instalador del sistema.",
            whereInApp = "Flujo de búsqueda/descarga de actualizaciones desde GitHub.",
            details = "Consulta GitHub Releases, interpreta la versión más reciente, cachea resultados/descargas y prepara el APK de actualización para entregarlo al " +
                "instalador del sistema. El trabajo de red/archivo está encapsulado y devuelve resultados estructurados a la UI. Esto permite cachear, validar y " +
                "manejar errores sin acoplar la pantalla a detalles de GitHub/HTTP. Entre sus símbolos más relevantes están object GitHubUpdateManager, class Release, " +
                "class CheckResult, class UpdateAvailable, class UpToDate, class NoPublishedRelease. El snapshot incluido corresponde a esta versión del proyecto y " +
                "contiene 228 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object GitHubUpdateManager", "class Release", "class CheckResult", "class UpdateAvailable", "class UpToDate", "class NoPublishedRelease", "class Failure", "fun currentVersionName"),
            internalDependencies = listOf(), lineCount = 228, assetName = "src_app_src_main_java_com_example_mynotes_update_githubupdatemanager_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Utilidades", title = "ExifOrientation.kt",
            path = "app/src/main/java/com/example/mynotes/util/ExifOrientation.kt",
            purpose = "Convierte la orientación EXIF de una imagen en una Matrix de rotación/espejo. Evita que fotos tomadas por cámara aparezcan giradas al decodificarse como bitmap.",
            whereInApp = "Carga/edición de imágenes, especialmente perfil y adjuntos.",
            details = "Convierte la orientación EXIF de una imagen en una Matrix de rotación/espejo. Evita que fotos tomadas por cámara aparezcan giradas al decodificarse " +
                "como bitmap. Contiene una transformación pequeña y determinista reutilizada por varias rutas. Mantenerla aislada reduce código repetido y facilita " +
                "probar el comportamiento sin levantar una pantalla completa. Entre sus símbolos más relevantes están fun exifOrientationMatrix. El snapshot incluido " +
                "corresponde a esta versión del proyecto y contiene 23 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun exifOrientationMatrix"),
            internalDependencies = listOf(), lineCount = 23, assetName = "src_app_src_main_java_com_example_mynotes_util_exiforientation_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Utilidades", title = "UriFiles.kt",
            path = "app/src/main/java/com/example/mynotes/util/UriFiles.kt",
            purpose = "Helpers pequeños para obtener nombre/metadata de un Uri usando ContentResolver y fallback de path. Evita repetir consultas OpenableColumns en selectores de archivos.",
            whereInApp = "Importación de adjuntos, PDF y otros archivos elegidos con SAF.",
            details = "Helpers pequeños para obtener nombre/metadata de un Uri usando ContentResolver y fallback de path. Evita repetir consultas OpenableColumns en " +
                "selectores de archivos. Contiene una transformación pequeña y determinista reutilizada por varias rutas. Mantenerla aislada reduce código repetido y " +
                "facilita probar el comportamiento sin levantar una pantalla completa. Entre sus símbolos más relevantes están fun Context. El snapshot incluido " +
                "corresponde a esta versión del proyecto y contiene 21 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun Context"),
            internalDependencies = listOf(), lineCount = 21, assetName = "src_app_src_main_java_com_example_mynotes_util_urifiles_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "ViewModels", title = "NoteViewModel.kt",
            path = "app/src/main/java/com/example/mynotes/viewmodel/NoteViewModel.kt",
            purpose = "ViewModel de notas y adjuntos. Expone Flows de Room, prepara/copia adjuntos, inserta/actualiza notas, coordina previews y solicita actualización de widgets después de cambios relevantes.",
            whereInApp = "NotesScreen, NoteEditorScreen y NoteDetailScreen consumen sus datos y acciones.",
            details = "ViewModel de notas y adjuntos. Expone Flows de Room, prepara/copia adjuntos, inserta/actualiza notas, coordina previews y solicita actualización de " +
                "widgets después de cambios relevantes. El ViewModel es el puente entre Flows/repositorios y Compose. Mantiene el trabajo en viewModelScope, expone " +
                "estado observable y evita que una recomposición lance directamente operaciones de base de datos. Entre sus símbolos más relevantes están class " +
                "NoteViewModel, fun launchWithWidgetUpdate, class PreparedAttachment, fun addNote, fun saveAttachments, fun prepareAttachments. Sus imports internos " +
                "muestran una conexión directa con app/src/main/java/com/example/mynotes/data/AppDatabase.kt, app/src/main/java/com/example/mynotes/data/Attachment.kt, " +
                "app/src/main/java/com/example/mynotes/data/Note.kt, app/src/main/java/com/example/mynotes/data/PendingAttachment.kt, " +
                "com.example.mynotes.util.openUriStream. El snapshot incluido corresponde a esta versión del proyecto y contiene 464 líneas; puede recorrerse dentro de " +
                "la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class NoteViewModel", "fun launchWithWidgetUpdate", "class PreparedAttachment", "fun addNote", "fun saveAttachments", "fun prepareAttachments", "fun prewarmAttachments", "fun copyAttachmentToInternalStorage"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/data/AppDatabase.kt", "app/src/main/java/com/example/mynotes/data/Attachment.kt", "app/src/main/java/com/example/mynotes/data/Note.kt", "app/src/main/java/com/example/mynotes/data/PendingAttachment.kt", "com.example.mynotes.util.openUriStream", "com.example.mynotes.util.uriDisplayName", "app/src/main/java/com/example/mynotes/performance/AttachmentPreviewCache.kt", "app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt"), lineCount = 464, assetName = "src_app_src_main_java_com_example_mynotes_viewmodel_noteviewmodel_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "ViewModels", title = "SettingsViewModel.kt",
            path = "app/src/main/java/com/example/mynotes/viewmodel/SettingsViewModel.kt",
            purpose = "Fachada de estado para SettingsRepository. Expone AppSettings como StateFlow y ofrece métodos simples para que Compose actualice preferencias sin conocer DataStore.",
            whereInApp = "SettingsScreen y MainActivity observan este ViewModel.",
            details = "Fachada de estado para SettingsRepository. Expone AppSettings como StateFlow y ofrece métodos simples para que Compose actualice preferencias sin " +
                "conocer DataStore. El ViewModel es el puente entre Flows/repositorios y Compose. Mantiene el trabajo en viewModelScope, expone estado observable y " +
                "evita que una recomposición lance directamente operaciones de base de datos. Entre sus símbolos más relevantes están class SettingsViewModel, fun " +
                "setConfigurationMode, fun setDarkMode, fun setBackgroundColor, fun setBackgroundToneIndex, fun setBackgroundIntensity. Sus imports internos muestran " +
                "una conexión directa con app/src/main/java/com/example/mynotes/settings/AppSettings.kt, " +
                "app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt, app/src/main/java/com/example/mynotes/widget/MyNotesWidgetUpdater.kt. El " +
                "snapshot incluido corresponde a esta versión del proyecto y contiene 153 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta " +
                "visualizarlo completo.",
            keySymbols = listOf("class SettingsViewModel", "fun setConfigurationMode", "fun setDarkMode", "fun setBackgroundColor", "fun setBackgroundToneIndex", "fun setBackgroundIntensity", "fun setSettingsPanelTone", "fun setSurfacePanelIntensity"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/settings/AppSettings.kt", "app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt", "app/src/main/java/com/example/mynotes/widget/MyNotesWidgetUpdater.kt"), lineCount = 153, assetName = "src_app_src_main_java_com_example_mynotes_viewmodel_settingsviewmodel_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Widgets", title = "FavoritesWidgetProvider.kt",
            path = "app/src/main/java/com/example/mynotes/widget/FavoritesWidgetProvider.kt",
            purpose = "AppWidgetProvider del widget de favoritos. Lee notas favoritas, tema y previews, llena RemoteViews y configura intents de apertura/acciones.",
            whereInApp = "Widget “Favoritos” del launcher.",
            details = "AppWidgetProvider del widget de favoritos. Lee notas favoritas, tema y previews, llena RemoteViews y configura intents de apertura/acciones. Los " +
                "widgets usan RemoteViews y viven fuera del árbol Compose. Estos archivos traducen datos/tema/intents a APIs de AppWidget, realizan I/O fuera del hilo " +
                "principal y solicitan refrescos cuando cambia la información. Entre sus símbolos más relevantes están class FavoritesWidgetProvider, fun onUpdate, fun " +
                "updateWidget. Sus imports internos muestran una conexión directa con com.example.mynotes.R, app/src/main/java/com/example/mynotes/data/AppDatabase.kt, " +
                "app/src/main/java/com/example/mynotes/data/Attachment.kt, app/src/main/java/com/example/mynotes/data/Note.kt. El snapshot incluido corresponde a esta " +
                "versión del proyecto y contiene 75 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class FavoritesWidgetProvider", "fun onUpdate", "fun updateWidget"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/data/AppDatabase.kt", "app/src/main/java/com/example/mynotes/data/Attachment.kt", "app/src/main/java/com/example/mynotes/data/Note.kt"), lineCount = 75, assetName = "src_app_src_main_java_com_example_mynotes_widget_favoriteswidgetprovider_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Widgets", title = "FocusNoteWidgetProvider.kt",
            path = "app/src/main/java/com/example/mynotes/widget/FocusNoteWidgetProvider.kt",
            purpose = "Widget centrado en una sola nota destacada. Selecciona la nota objetivo, aplica tema, preview multimedia y acciones directas.",
            whereInApp = "Widget “Nota enfocada” del launcher.",
            details = "Widget centrado en una sola nota destacada. Selecciona la nota objetivo, aplica tema, preview multimedia y acciones directas. Los widgets usan " +
                "RemoteViews y viven fuera del árbol Compose. Estos archivos traducen datos/tema/intents a APIs de AppWidget, realizan I/O fuera del hilo principal y " +
                "solicitan refrescos cuando cambia la información. Entre sus símbolos más relevantes están class FocusNoteWidgetProvider, fun onUpdate. Sus imports " +
                "internos muestran una conexión directa con com.example.mynotes.R, app/src/main/java/com/example/mynotes/data/AppDatabase.kt. El snapshot incluido " +
                "corresponde a esta versión del proyecto y contiene 84 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class FocusNoteWidgetProvider", "fun onUpdate"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/data/AppDatabase.kt"), lineCount = 84, assetName = "src_app_src_main_java_com_example_mynotes_widget_focusnotewidgetprovider_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Widgets", title = "MyNotesWidgetUpdater.kt",
            path = "app/src/main/java/com/example/mynotes/widget/MyNotesWidgetUpdater.kt",
            purpose = "Coordinador global de refresco de widgets. Agrupa solicitudes con un pequeño debounce/coalescing y actualiza todos los providers registrados sin repetir trabajo por cada cambio de nota.",
            whereInApp = "Se invoca tras crear/editar/eliminar notas y desde restauraciones/acciones de widget.",
            details = "Coordinador global de refresco de widgets. Agrupa solicitudes con un pequeño debounce/coalescing y actualiza todos los providers registrados sin " +
                "repetir trabajo por cada cambio de nota. Los widgets usan RemoteViews y viven fuera del árbol Compose. Estos archivos traducen datos/tema/intents a " +
                "APIs de AppWidget, realizan I/O fuera del hilo principal y solicitan refrescos cuando cambia la información. Entre sus símbolos más relevantes están " +
                "object MyNotesWidgetUpdater, fun requestUpdate, fun updateProvider. El snapshot incluido corresponde a esta versión del proyecto y contiene 65 líneas; " +
                "puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object MyNotesWidgetUpdater", "fun requestUpdate", "fun updateProvider"),
            internalDependencies = listOf(), lineCount = 65, assetName = "src_app_src_main_java_com_example_mynotes_widget_mynoteswidgetupdater_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Widgets", title = "QuickNoteWidgetProvider.kt",
            path = "app/src/main/java/com/example/mynotes/widget/QuickNoteWidgetProvider.kt",
            purpose = "Provider del widget de acción rápida para crear una nota. Construye RemoteViews temáticas y el PendingIntent que abre directamente el flujo de nueva nota.",
            whereInApp = "Widget “Nueva nota” del launcher.",
            details = "Provider del widget de acción rápida para crear una nota. Construye RemoteViews temáticas y el PendingIntent que abre directamente el flujo de nueva " +
                "nota. Los widgets usan RemoteViews y viven fuera del árbol Compose. Estos archivos traducen datos/tema/intents a APIs de AppWidget, realizan I/O fuera " +
                "del hilo principal y solicitan refrescos cuando cambia la información. Entre sus símbolos más relevantes están class QuickNoteWidgetProvider, fun " +
                "onUpdate. Sus imports internos muestran una conexión directa con com.example.mynotes.R. El snapshot incluido corresponde a esta versión del proyecto y " +
                "contiene 36 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class QuickNoteWidgetProvider", "fun onUpdate"),
            internalDependencies = listOf("com.example.mynotes.R"), lineCount = 36, assetName = "src_app_src_main_java_com_example_mynotes_widget_quicknotewidgetprovider_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Widgets", title = "RecentNotesWidgetProvider.kt",
            path = "app/src/main/java/com/example/mynotes/widget/RecentNotesWidgetProvider.kt",
            purpose = "Provider de notas recientes. Consulta una selección de notas, construye filas y previews, aplica tema y enlaza cada fila con la nota correspondiente.",
            whereInApp = "Widget “Notas recientes” del launcher.",
            details = "Provider de notas recientes. Consulta una selección de notas, construye filas y previews, aplica tema y enlaza cada fila con la nota correspondiente. " +
                "Los widgets usan RemoteViews y viven fuera del árbol Compose. Estos archivos traducen datos/tema/intents a APIs de AppWidget, realizan I/O fuera del " +
                "hilo principal y solicitan refrescos cuando cambia la información. Entre sus símbolos más relevantes están class RecentNotesWidgetProvider, fun " +
                "onUpdate, fun updateWidget. Sus imports internos muestran una conexión directa con com.example.mynotes.R, " +
                "app/src/main/java/com/example/mynotes/data/AppDatabase.kt, app/src/main/java/com/example/mynotes/data/Attachment.kt, " +
                "app/src/main/java/com/example/mynotes/data/Note.kt. El snapshot incluido corresponde a esta versión del proyecto y contiene 80 líneas; puede " +
                "recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("class RecentNotesWidgetProvider", "fun onUpdate", "fun updateWidget"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/data/AppDatabase.kt", "app/src/main/java/com/example/mynotes/data/Attachment.kt", "app/src/main/java/com/example/mynotes/data/Note.kt"), lineCount = 80, assetName = "src_app_src_main_java_com_example_mynotes_widget_recentnoteswidgetprovider_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Widgets", title = "WidgetActionReceiver.kt",
            path = "app/src/main/java/com/example/mynotes/widget/WidgetActionReceiver.kt",
            purpose = "Receiver privado para acciones que no necesitan abrir Activity, como alternar favorito/fijado. Ejecuta la operación en segundo plano y solicita refresco del widget.",
            whereInApp = "Botones interactivos dentro de widgets.",
            details = "Receiver privado para acciones que no necesitan abrir Activity, como alternar favorito/fijado. Ejecuta la operación en segundo plano y solicita " +
                "refresco del widget. Los widgets usan RemoteViews y viven fuera del árbol Compose. Estos archivos traducen datos/tema/intents a APIs de AppWidget, " +
                "realizan I/O fuera del hilo principal y solicitan refrescos cuando cambia la información. Entre sus símbolos más relevantes están class " +
                "WidgetActionReceiver, fun onReceive. Sus imports internos muestran una conexión directa con app/src/main/java/com/example/mynotes/data/AppDatabase.kt. " +
                "El snapshot incluido corresponde a esta versión del proyecto y contiene 28 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta " +
                "visualizarlo completo.",
            keySymbols = listOf("class WidgetActionReceiver", "fun onReceive"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/data/AppDatabase.kt"), lineCount = 28, assetName = "src_app_src_main_java_com_example_mynotes_widget_widgetactionreceiver_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Widgets", title = "WidgetActions.kt",
            path = "app/src/main/java/com/example/mynotes/widget/WidgetActions.kt",
            purpose = "Constantes de acciones y extras compartidas por providers, intents y receiver. Mantiene nombres estables para que todos los componentes de widget hablen el mismo protocolo.",
            whereInApp = "Infraestructura interna de todos los widgets.",
            details = "Constantes de acciones y extras compartidas por providers, intents y receiver. Mantiene nombres estables para que todos los componentes de widget " +
                "hablen el mismo protocolo. Los widgets usan RemoteViews y viven fuera del árbol Compose. Estos archivos traducen datos/tema/intents a APIs de " +
                "AppWidget, realizan I/O fuera del hilo principal y solicitan refrescos cuando cambia la información. Entre sus símbolos más relevantes están object " +
                "WidgetActions. El snapshot incluido corresponde a esta versión del proyecto y contiene 22 líneas; puede recorrerse dentro de la app en bloques de 160 " +
                "líneas hasta visualizarlo completo.",
            keySymbols = listOf("object WidgetActions"),
            internalDependencies = listOf(), lineCount = 22, assetName = "src_app_src_main_java_com_example_mynotes_widget_widgetactions_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Widgets", title = "WidgetAsync.kt",
            path = "app/src/main/java/com/example/mynotes/widget/WidgetAsync.kt",
            purpose = "Helper para ejecutar trabajo coroutine desde BroadcastReceiver usando goAsync y asegurar finish() al terminar. Evita bloquear onReceive y respeta el ciclo de vida del receiver.",
            whereInApp = "WidgetActionReceiver y providers/receivers que necesitan trabajo asíncrono.",
            details = "Helper para ejecutar trabajo coroutine desde BroadcastReceiver usando goAsync y asegurar finish() al terminar. Evita bloquear onReceive y respeta el " +
                "ciclo de vida del receiver. Los widgets usan RemoteViews y viven fuera del árbol Compose. Estos archivos traducen datos/tema/intents a APIs de " +
                "AppWidget, realizan I/O fuera del hilo principal y solicitan refrescos cuando cambia la información. Entre sus símbolos más relevantes están fun " +
                "BroadcastReceiver. El snapshot incluido corresponde a esta versión del proyecto y contiene 14 líneas; puede recorrerse dentro de la app en bloques de " +
                "160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("fun BroadcastReceiver"),
            internalDependencies = listOf(), lineCount = 14, assetName = "src_app_src_main_java_com_example_mynotes_widget_widgetasync_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Widgets", title = "WidgetIntents.kt",
            path = "app/src/main/java/com/example/mynotes/widget/WidgetIntents.kt",
            purpose = "Fábrica de Intent/PendingIntent para abrir app, crear nota, abrir nota/colección, buscar o alternar estados. Centraliza flags y request codes para evitar colisiones.",
            whereInApp = "Todas las superficies RemoteViews de widgets.",
            details = "Fábrica de Intent/PendingIntent para abrir app, crear nota, abrir nota/colección, buscar o alternar estados. Centraliza flags y request codes para " +
                "evitar colisiones. Los widgets usan RemoteViews y viven fuera del árbol Compose. Estos archivos traducen datos/tema/intents a APIs de AppWidget, " +
                "realizan I/O fuera del hilo principal y solicitan refrescos cuando cambia la información. Entre sus símbolos más relevantes están object " +
                "WidgetIntents, fun openApp, fun newNote, fun openNote, fun openCollection, fun search. Sus imports internos muestran una conexión directa con " +
                "app/src/main/java/com/example/mynotes/MainActivity.kt. El snapshot incluido corresponde a esta versión del proyecto y contiene 78 líneas; puede " +
                "recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object WidgetIntents", "fun openApp", "fun newNote", "fun openNote", "fun openCollection", "fun search", "fun toggleFavorite", "fun togglePin"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/MainActivity.kt"), lineCount = 78, assetName = "src_app_src_main_java_com_example_mynotes_widget_widgetintents_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Widgets", title = "WidgetLocale.kt",
            path = "app/src/main/java/com/example/mynotes/widget/WidgetLocale.kt",
            purpose = "Resuelve el idioma guardado y crea un Context localizado para widgets, que se renderizan fuera de la composición normal de MainActivity.",
            whereInApp = "Textos de widgets cuando el usuario cambia el idioma de MyNotes.",
            details = "Resuelve el idioma guardado y crea un Context localizado para widgets, que se renderizan fuera de la composición normal de MainActivity. Los widgets " +
                "usan RemoteViews y viven fuera del árbol Compose. Estos archivos traducen datos/tema/intents a APIs de AppWidget, realizan I/O fuera del hilo " +
                "principal y solicitan refrescos cuando cambia la información. Entre sus símbolos más relevantes están object WidgetLocale, fun selectedLanguage, fun " +
                "localizedContext, fun locale. El snapshot incluido corresponde a esta versión del proyecto y contiene 49 líneas; puede recorrerse dentro de la app en " +
                "bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object WidgetLocale", "fun selectedLanguage", "fun localizedContext", "fun locale"),
            internalDependencies = listOf(), lineCount = 49, assetName = "src_app_src_main_java_com_example_mynotes_widget_widgetlocale_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Widgets", title = "WidgetMediaPreview.kt",
            path = "app/src/main/java/com/example/mynotes/widget/WidgetMediaPreview.kt",
            purpose = "Obtiene la mejor miniatura para RemoteViews: adjunto visual, preview cacheado de enlace o placeholder. También administra claves/hash/rutas de caché compatibles con widgets.",
            whereInApp = "Widgets de notas recientes, favoritos y nota enfocada.",
            details = "Obtiene la mejor miniatura para RemoteViews: adjunto visual, preview cacheado de enlace o placeholder. También administra claves/hash/rutas de caché " +
                "compatibles con widgets. Los widgets usan RemoteViews y viven fuera del árbol Compose. Estos archivos traducen datos/tema/intents a APIs de AppWidget, " +
                "realizan I/O fuera del hilo principal y solicitan refrescos cuando cambia la información. Entre sus símbolos más relevantes están object " +
                "WidgetMediaPreview, fun firstVisualByNote, fun loadBestPreviewBitmap, fun cachedLinkPreviewFile, fun sha256, fun loadPreviewBitmap. Sus imports " +
                "internos muestran una conexión directa con app/src/main/java/com/example/mynotes/data/Attachment.kt, " +
                "app/src/main/java/com/example/mynotes/data/Note.kt, app/src/main/java/com/example/mynotes/links/LinkPreviewRepository.kt. El snapshot incluido " +
                "corresponde a esta versión del proyecto y contiene 142 líneas; puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object WidgetMediaPreview", "fun firstVisualByNote", "fun loadBestPreviewBitmap", "fun cachedLinkPreviewFile", "fun sha256", "fun loadPreviewBitmap", "fun loadVideoFrame", "fun decodeSampledBitmap"),
            internalDependencies = listOf("app/src/main/java/com/example/mynotes/data/Attachment.kt", "app/src/main/java/com/example/mynotes/data/Note.kt", "app/src/main/java/com/example/mynotes/links/LinkPreviewRepository.kt"), lineCount = 142, assetName = "src_app_src_main_java_com_example_mynotes_widget_widgetmediapreview_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Widgets", title = "WidgetPresentation.kt",
            path = "app/src/main/java/com/example/mynotes/widget/WidgetPresentation.kt",
            purpose = "Capa de presentación compartida de widgets. Normaliza contenido visible, construye especificación de tema, colores, textos, badges, metadatos y binding de filas/miniaturas.",
            whereInApp = "Todos los widgets reutilizan estas funciones para verse coherentes con la app.",
            details = "Capa de presentación compartida de widgets. Normaliza contenido visible, construye especificación de tema, colores, textos, badges, metadatos y " +
                "binding de filas/miniaturas. Los widgets usan RemoteViews y viven fuera del árbol Compose. Estos archivos traducen datos/tema/intents a APIs de " +
                "AppWidget, realizan I/O fuera del hilo principal y solicitan refrescos cuando cambia la información. Entre sus símbolos más relevantes están object " +
                "WidgetPresentation, fun visibleNoteContent, class WidgetThemeSpec, fun theme, fun themedRoot, fun accentColor. Sus imports internos muestran una " +
                "conexión directa con com.example.mynotes.R, app/src/main/java/com/example/mynotes/data/Attachment.kt, " +
                "app/src/main/java/com/example/mynotes/data/Note.kt, app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt, " +
                "app/src/main/java/com/example/mynotes/ui/theme/PaletteCatalog.kt. El snapshot incluido corresponde a esta versión del proyecto y contiene 288 líneas; " +
                "puede recorrerse dentro de la app en bloques de 160 líneas hasta visualizarlo completo.",
            keySymbols = listOf("object WidgetPresentation", "fun visibleNoteContent", "class WidgetThemeSpec", "fun theme", "fun themedRoot", "fun accentColor", "fun bindNoteRow", "fun noteAccentColor"),
            internalDependencies = listOf("com.example.mynotes.R", "app/src/main/java/com/example/mynotes/data/Attachment.kt", "app/src/main/java/com/example/mynotes/data/Note.kt", "app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt", "app/src/main/java/com/example/mynotes/ui/theme/PaletteCatalog.kt"), lineCount = 288, assetName = "src_app_src_main_java_com_example_mynotes_widget_widgetpresentation_kt.txt"
        ),
        DevelopmentFileDoc(
            category = "Build y manifiesto", title = "build.gradle.kts",
            path = "app/build.gradle.kts",
            purpose = "Define plugins, SDK, buildTypes, R8/resource shrinking y todas las dependencias Android/Compose/Room/Media3/ML Kit usadas por el módulo app.",
            whereInApp = "Afecta a toda la aplicación durante compilación; las decisiones release de minificación explican por qué Recorte IA necesitó reglas de conservación específicas.",
            details = "Define plugins, SDK, buildTypes, R8/resource shrinking y todas las dependencias Android/Compose/Room/Media3/ML Kit usadas por el módulo app. No es " +
                "código de pantalla, pero determina qué entra en el APK, qué componentes conoce Android y qué optimizaciones/permisos se aplican. Un cambio aquí puede " +
                "alterar el comportamiento release incluso si el Kotlin permanece idéntico. El snapshot contiene 88 líneas y se muestra completo mediante el mismo " +
                "visor paginado.",
            keySymbols = listOf(),
            internalDependencies = listOf(), lineCount = 88, assetName = "cfg_app_build_gradle_kts.txt"
        ),
        DevelopmentFileDoc(
            category = "Build y manifiesto", title = "proguard-rules.pro",
            path = "app/proguard-rules.pro",
            purpose = "Reglas propias de R8/ProGuard. Conserva metadatos y las clases de ML Kit/Subject Segmentation/ModuleInstall que participan en carga dinámica.",
            whereInApp = "Sólo influye especialmente en builds release minificados/firmados; no dibuja UI pero puede decidir si una función sigue disponible después de optimizar el APK.",
            details = "Reglas propias de R8/ProGuard. Conserva metadatos y las clases de ML Kit/Subject Segmentation/ModuleInstall que participan en carga dinámica. No es " +
                "código de pantalla, pero determina qué entra en el APK, qué componentes conoce Android y qué optimizaciones/permisos se aplican. Un cambio aquí puede " +
                "alterar el comportamiento release incluso si el Kotlin permanece idéntico. El snapshot contiene 22 líneas y se muestra completo mediante el mismo " +
                "visor paginado.",
            keySymbols = listOf(),
            internalDependencies = listOf(), lineCount = 22, assetName = "cfg_app_proguard_rules_pro.txt"
        ),
        DevelopmentFileDoc(
            category = "Build y manifiesto", title = "AndroidManifest.xml",
            path = "app/src/main/AndroidManifest.xml",
            purpose = "Declara permisos, Activities, receivers de recordatorios/widgets, FileProvider, filtros de compartir y el meta-data de ML Kit subject_segment.",
            whereInApp = "Android lee este archivo al instalar/ejecutar el APK; define qué componentes pueden iniciarse y qué capacidades del sistema solicita MyNotes.",
            details = "Declara permisos, Activities, receivers de recordatorios/widgets, FileProvider, filtros de compartir y el meta-data de ML Kit subject_segment. No es " +
                "código de pantalla, pero determina qué entra en el APK, qué componentes conoce Android y qué optimizaciones/permisos se aplican. Un cambio aquí puede " +
                "alterar el comportamiento release incluso si el Kotlin permanece idéntico. El snapshot contiene 200 líneas y se muestra completo mediante el mismo " +
                "visor paginado.",
            keySymbols = listOf(),
            internalDependencies = listOf(), lineCount = 200, assetName = "cfg_app_src_main_androidmanifest_xml.txt"
        ),
        DevelopmentFileDoc(
            category = "Recursos Android", title = "Strings, temas y valores",
            path = "app/src/main/res/values/ + app/src/main/res/values-en/ + app/src/main/res/values-es/ + app/src/main/res/values-fr/ + app/src/main/res/values-zh-rCN/ + app/src/main/res/values-v26/ + app/src/main/res/values-v31/",
            purpose = "Familias values* que contienen textos localizados, estilos, colores/dimensiones y overrides por API. Se agrupan porque Android los selecciona por configuración/idioma, no porque sean lógica Kotlin.",
            whereInApp = "Toda la app: labels, mensajes, traducciones, estilos y valores que Android resuelve mediante R.",
            details = "Familias values* que contienen textos localizados, estilos, colores/dimensiones y overrides por API. Se agrupan porque Android los selecciona por " +
                "configuración/idioma, no porque sean lógica Kotlin. El bloque de código de esta tarjeta es un inventario real de los archivos incluidos en esta " +
                "familia para que se pueda revisar el resto de recursos sin ocultarlos. Esta familia contiene 79 archivos en V206.",
            keySymbols = listOf(),
            internalDependencies = listOf(), lineCount = 79, assetName = "res_strings_temas_y_valores.txt"
        ),
        DevelopmentFileDoc(
            category = "Recursos Android", title = "Layouts de widgets y RemoteViews",
            path = "app/src/main/res/layout/",
            purpose = "XML de layout usado sobre todo por AppWidget/RemoteViews. Compose no necesita layouts XML para sus pantallas principales, pero los widgets del launcher sí.",
            whereInApp = "Widgets de nueva nota, recientes, favoritos, nota enfocada y sus variantes/tarjetas.",
            details = "XML de layout usado sobre todo por AppWidget/RemoteViews. Compose no necesita layouts XML para sus pantallas principales, pero los widgets del " +
                "launcher sí. El bloque de código de esta tarjeta es un inventario real de los archivos incluidos en esta familia para que se pueda revisar el resto de " +
                "recursos sin ocultarlos. Esta familia contiene 53 archivos en V206.",
            keySymbols = listOf(),
            internalDependencies = listOf(), lineCount = 53, assetName = "res_layouts_de_widgets_y_remoteviews.txt"
        ),
        DevelopmentFileDoc(
            category = "Recursos Android", title = "Drawables y gráficos",
            path = "app/src/main/res/drawable/ + app/src/main/res/drawable-nodpi/",
            purpose = "Formas, fondos, iconos y recursos gráficos que Android puede reutilizar desde R.drawable; incluye assets visuales de widgets y superficies.",
            whereInApp = "Widgets, notificaciones, iconografía/fondos y elementos que no se generan directamente con Compose.",
            details = "Formas, fondos, iconos y recursos gráficos que Android puede reutilizar desde R.drawable; incluye assets visuales de widgets y superficies. El bloque " +
                "de código de esta tarjeta es un inventario real de los archivos incluidos en esta familia para que se pueda revisar el resto de recursos sin " +
                "ocultarlos. Esta familia contiene 321 archivos en V206.",
            keySymbols = listOf(),
            internalDependencies = listOf(), lineCount = 321, assetName = "res_drawables_y_gr_ficos.txt"
        ),
        DevelopmentFileDoc(
            category = "Recursos Android", title = "Iconos de launcher",
            path = "app/src/main/res/mipmap-anydpi-v26/ + app/src/main/res/mipmap-hdpi/ + app/src/main/res/mipmap-mdpi/ + app/src/main/res/mipmap-xhdpi/ + app/src/main/res/mipmap-xxhdpi/ + app/src/main/res/mipmap-xxxhdpi/",
            purpose = "Variantes adaptativas y rasterizadas del icono de aplicación para distintas densidades/API. Android elige automáticamente la mejor variante.",
            whereInApp = "Launcher, información de aplicación y superficies del sistema que muestran el icono de MyNotes.",
            details = "Variantes adaptativas y rasterizadas del icono de aplicación para distintas densidades/API. Android elige automáticamente la mejor variante. El bloque " +
                "de código de esta tarjeta es un inventario real de los archivos incluidos en esta familia para que se pueda revisar el resto de recursos sin " +
                "ocultarlos. Esta familia contiene 22 archivos en V206.",
            keySymbols = listOf(),
            internalDependencies = listOf(), lineCount = 22, assetName = "res_iconos_de_launcher.txt"
        ),
        DevelopmentFileDoc(
            category = "Recursos Android", title = "Configuración XML del sistema",
            path = "app/src/main/res/xml/",
            purpose = "XML declarativo para FileProvider, backups, data extraction y AppWidgetProvider. Son contratos que Android lee fuera de Compose.",
            whereInApp = "Compartir/abrir archivos, backup del sistema y metadatos de widgets.",
            details = "XML declarativo para FileProvider, backups, data extraction y AppWidgetProvider. Son contratos que Android lee fuera de Compose. El bloque de código " +
                "de esta tarjeta es un inventario real de los archivos incluidos en esta familia para que se pueda revisar el resto de recursos sin ocultarlos. Esta " +
                "familia contiene 7 archivos en V206.",
            keySymbols = listOf(),
            internalDependencies = listOf(), lineCount = 7, assetName = "res_configuraci_n_xml_del_sistema.txt"
        ),
        DevelopmentFileDoc(
            category = "Recursos Android", title = "Recursos raw de audio",
            path = "app/src/main/res/raw/",
            purpose = "Archivos raw empaquetados sin transformación principal, usados por los temas sonoros/tonos de feedback y recordatorios.",
            whereInApp = "UiSoundPlayer, ReminderFeedbackPreferences y notificaciones de recordatorio.",
            details = "Archivos raw empaquetados sin transformación principal, usados por los temas sonoros/tonos de feedback y recordatorios. El bloque de código de esta " +
                "tarjeta es un inventario real de los archivos incluidos en esta familia para que se pueda revisar el resto de recursos sin ocultarlos. Esta familia " +
                "contiene 200 archivos en V206.",
            keySymbols = listOf(),
            internalDependencies = listOf(), lineCount = 200, assetName = "res_recursos_raw_de_audio.txt"
        ),
    )
}

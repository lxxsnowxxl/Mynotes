MYNOTES - UI BASADA EN EL MOCKUP APROBADO
==========================================

OBJETIVO
--------
Este paquete implementa el diseño aprobado:

1. PANTALLA PRINCIPAL
   - "My notes / Mis notas"
   - subtítulo
   - buscador real por título y contenido
   - chips:
       All / Todas
       Favorites / Favoritas
       Work / Trabajo
       Personal
       Images / Imágenes
       Files / Archivos
   - tarjetas tipo catálogo
   - imagen superior cuando la nota tiene imagen
   - estrella de favorita
   - pin
   - categoría
   - fecha
   - menú de edición, prioridad, color, categoría y eliminación
   - FAB circular para crear una nota

2. DETALLE
   - título grande
   - favorita
   - prioridad
   - categoría
   - fecha
   - contenido
   - grid de adjuntos
   - thumbnails cacheados para video/PDF/audio cuando están disponibles
   - barra inferior:
       Share / Compartir
       Pin / Fijar
       Move / Mover
       Delete / Eliminar
   - botón + en Adjuntos abre la edición de la nota

3. SETTINGS
   - tarjeta "Appearance / Apariencia"
   - 6 paletas:
       Neutral
       Warm / Cálida
       Sage / Salvia
       Ocean / Océano
       Lavender / Lavanda
       Rose / Rosa
   - CADA PALETA TIENE EXACTAMENTE 4 TONOS
   - CADA UNO DE LOS 4 CÍRCULOS ES SELECCIONABLE INDIVIDUALMENTE
   - el círculo activo muestra ✓
   - Palette tone / Tono de la paleta conserva su slider discreto 1/4..4/4
   - intensidad de fondo
   - intensidad del encabezado
   - texto general Black / White
   - texto independiente de notas y menú Black / White
   - fuente
   - tamaño
   - idioma
   - columnas 1..3
   - los 10 diseños de sliders
   - modo oscuro

4. DATOS
   - Note añade:
       category
       isFavorite
       isPinned
   - Room pasa a versión 5
   - migraciones 3 -> 4 -> 5 sin borrar notas
   - índices para pin/prioridad/fecha y categoría

5. RENDIMIENTO CONSERVADO
   - AttachmentPreviewCache
   - caché RAM + disco
   - previews multimedia limitados
   - inserción de adjuntos por lote
   - lifecycle-aware state
   - R8 + shrinkResources en release
   - MainActivity conserva 60 Hz preferido y barra inferior Android transitoria

ARCHIVOS QUE DEBES REEMPLAZAR
-----------------------------
app/build.gradle.kts
app/proguard-rules.pro
app/src/main/AndroidManifest.xml

app/src/main/java/com/example/mynotes/MainActivity.kt

app/src/main/java/com/example/mynotes/data/Note.kt
app/src/main/java/com/example/mynotes/data/Attachment.kt
app/src/main/java/com/example/mynotes/data/NoteDao.kt
app/src/main/java/com/example/mynotes/data/AttachmentDao.kt
app/src/main/java/com/example/mynotes/data/AppDatabase.kt

app/src/main/java/com/example/mynotes/settings/AppSettings.kt
app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt

app/src/main/java/com/example/mynotes/viewmodel/SettingsViewModel.kt
app/src/main/java/com/example/mynotes/viewmodel/NoteViewModel.kt

app/src/main/java/com/example/mynotes/ui/theme/Theme.kt

En tu proyecto actual las pantallas físicamente aparecen dentro de ui/theme,
aunque su package es com.example.mynotes.ui. Para evitar duplicados, reemplaza:

app/src/main/java/com/example/mynotes/ui/theme/NotesScreen.kt
app/src/main/java/com/example/mynotes/ui/theme/NoteDetailScreen.kt
app/src/main/java/com/example/mynotes/ui/theme/SettingsScreen.kt

ARCHIVOS NUEVOS
---------------
app/src/main/java/com/example/mynotes/ui/theme/PaletteCatalog.kt
app/src/main/java/com/example/mynotes/ui/theme/AppFonts.kt
app/src/main/java/com/example/mynotes/ui/theme/NoteColors.kt

app/src/main/java/com/example/mynotes/ui/components/PaletteSelector.kt
app/src/main/java/com/example/mynotes/ui/components/StyledSettingsSlider.kt
app/src/main/java/com/example/mynotes/ui/components/NoteCard.kt
app/src/main/java/com/example/mynotes/ui/components/AttachmentPreviewTile.kt

app/src/main/res/values/strings_mockup.xml
app/src/main/res/values-es/strings_mockup.xml
app/src/main/res/values-en/strings_mockup.xml
app/src/main/res/values-fr/strings_mockup.xml

SE CONSERVAN SIN REEMPLAZAR
---------------------------
NoteEditorScreen.kt
PendingAttachment.kt
Color.kt
Type.kt
los archivos Google Sans en res/font
file_paths.xml
themes.xml / splash
backup_rules.xml
data_extraction_rules.xml

IMPORTANTE SOBRE ROOM
---------------------
No desinstales MyNotes para probar la migración.

Si tu base actual está en versión 3:
3 -> 4 -> 5

Si ya pasó por la optimización anterior:
4 -> 5

La migración 4 -> 5 solamente agrega category, isFavorite e isPinned.
No recrea la tabla de notas.

INSTALACIÓN
-----------
1. Haz una copia de tu carpeta MyNotes2.
2. Extrae este ZIP.
3. Copia la carpeta "app" encima de la carpeta "app" de MyNotes2.
4. Asegúrate de NO dejar una segunda copia de NotesScreen.kt,
   NoteDetailScreen.kt o SettingsScreen.kt en otra ruta con el mismo package.
5. Android Studio:
      File > Sync Project with Gradle Files
      Build > Clean Project
      Build > Rebuild Project
6. Ejecuta la app sin desinstalar la versión anterior.
7. Comprueba:
      - notas existentes
      - selección de los cuatro tonos
      - favorita
      - pin
      - Work / Personal
      - búsqueda
      - filtros
      - adjuntos
      - edición
8. Después genera el APK release.

NOTA
----
El avatar del mockup se representa con un icono Person porque MyNotes todavía
no tiene sistema de cuentas/perfiles. Ese botón abre Settings.

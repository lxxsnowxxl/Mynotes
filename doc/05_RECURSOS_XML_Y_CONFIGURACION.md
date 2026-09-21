# Recursos XML, Manifest y configuración

## AndroidManifest

Permisos actuales detectados: `INTERNET`, `RECORD_AUDIO`, `VIBRATE`, `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED`, `MODIFY_AUDIO_SETTINGS`, `REQUEST_INSTALL_PACKAGES`. El micrófono se declara como feature opcional.

Activities: `MainActivity` y `AttachmentViewerActivity`.

Receivers funcionales: `ReminderReceiver`, `QuickNoteWidgetProvider`, `RecentNotesWidgetProvider`, `FavoritesWidgetProvider`, `FocusNoteWidgetProvider`, `WidgetActionReceiver`.

Provider: `androidx.core.content.FileProvider`.

## Recursos

Total de archivos en `src/main/res`: **632**. Principales directorios: `drawable`=316, `drawable-nodpi`=5, `font`=7, `layout`=46, `mipmap-anydpi-v26`=2, `mipmap-hdpi`=4, `mipmap-mdpi`=4, `mipmap-xhdpi`=4, `mipmap-xxhdpi`=4, `mipmap-xxxhdpi`=4, `raw`=156, `values`=19, `values-en`=13, `values-es`=13, `values-fr`=13, `values-v26`=2, `values-v31`=1, `values-zh-rCN`=12, `xml`=7.

Localizaciones: `values, values-en, values-es, values-fr, values-v26, values-v31, values-zh-rCN`.

Los widgets Collage/Overview no están registrados. Existen cuatro cadenas legacy (`widget_note_collage*`, `widget_stats*`) únicamente como compatibilidad frente a XML antiguos que puedan quedar al descomprimir encima de una carpeta previa.

Inventario completo: `inventarios/RECURSOS.csv`.

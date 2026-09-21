# Mapa de arquitectura actual de MyNotes

```text
MainActivity
├─ SettingsViewModel → SettingsRepository → DataStore/AppSettings
├─ NoteViewModel → Room (AppDatabase → NoteDao / AttachmentDao)
│  └─ AttachmentPreviewCache
├─ NotesScreen
│  ├─ NoteCard → adjuntos + LinkPreviewCard
│  └─ speed dial → Recordatorios / Nueva nota / Dibujar
├─ NoteEditorScreen → PendingAttachment / grabación / paleta
├─ DrawingScreen → trazos / herramientas / color de lienzo / PNG
├─ ReminderScreen
│  └─ ReminderRepository → ReminderAlarmScheduler
│     └─ ReminderReceiver → NotificationManager
├─ SettingsScreen → ExtremeCustomization / BackupRestore / Update
├─ GitHubUpdateManager → GitHub Releases → FileProvider/instalador
├─ Widget providers (4) → WidgetPresentation / WidgetLocale / WidgetMediaPreview
└─ AttachmentViewerActivity
```

## Fuentes de verdad
- Notas/adjuntos persistentes: Room + almacenamiento privado de archivos.
- Configuración: DataStore.
- Recordatorios: ReminderRepository y alarmas programadas.
- Previews/miniaturas: datos derivados y reconstruibles; no sustituyen el adjunto original.

## Compatibilidad importante
La app declara minSdk 24. Varias decisiones (RemoteViews sin tint complejo, notificación colapsada nativa, comportamiento de AlarmManager) conservan compatibilidad con Samsung/API 28 además de APIs recientes.

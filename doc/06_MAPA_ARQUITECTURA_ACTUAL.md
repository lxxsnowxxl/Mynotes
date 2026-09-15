# Mapa de arquitectura actual de MyNotes

```text
MainActivity
├─ SettingsViewModel ── SettingsRepository ── DataStore
├─ NoteViewModel ────── Room (NoteDao / AttachmentDao)
│  └─ AttachmentPreviewCache ── RAM LRU + cacheDir
├─ NotesScreen
│  └─ ModernNoteCard
│     ├─ previews de adjuntos
│     └─ previews de enlaces
├─ NoteEditorScreen
│  ├─ adjuntos pendientes
│  ├─ grabación de voz
│  └─ paleta de color centrada/adaptativa
├─ SettingsScreen
│  └─ DevelopmentInfoScreen
│     └─ SourceCodeInfoScreen
└─ AttachmentViewerActivity
```

## Flujo de una miniatura

1. El adjunto original queda persistido de forma privada en `filesDir/attachments`.
2. `NoteViewModel` puede solicitar `prewarm` después de guardar/copiar el adjunto.
3. `AttachmentPreviewCache` elige perfil según `performanceMode`.
4. La variante derivada se guarda en `cacheDir` y/o LRU de memoria.
5. `NotesScreen`/`NoteCard` consultan previews ya disponibles para evitar trabajo pesado en el frame de scroll.
6. En máxima calidad puede existir una representación `instant` pequeña y una final de alta resolución.

## Flujo del teclado y sonido

1. `MainActivity` observa `WindowInsetsCompat.Type.ime()`.
2. Si el IME aparece y la política está habilitada, recuerda el estado previo de `STREAM_SYSTEM`.
3. Silencia temporalmente ese stream.
4. `UiSoundPlayer` mantiene los efectos de MyNotes por una ruta distinta.
5. Al ocultar IME/salir del estado, la Activity restaura únicamente lo que ella modificó.

## Invariante central

Los caches son **derivados y reconstruibles**. El adjunto persistente es la fuente de verdad. Borrar/limpiar cache no debe borrar el archivo original de la nota.

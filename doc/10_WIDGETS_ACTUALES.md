# Widgets actuales

El Manifest registra exactamente cuatro widgets:

1. QuickNoteWidgetProvider — Nota rápida.
2. RecentNotesWidgetProvider — Notas recientes.
3. FavoritesWidgetProvider — Favoritas.
4. FocusNoteWidgetProvider — Nota destacada/en foco.

## Infraestructura compartida
- `MyNotesWidgetUpdater`: refrescos agrupados.
- `WidgetPresentation`: colores/paleta/textos.
- `WidgetLocale`: aplica el idioma elegido dentro de MyNotes en RemoteViews.
- `WidgetMediaPreview`: prepara media sin cambiar el pipeline principal de adjuntos.
- `WidgetIntents`, `WidgetActions`, `WidgetActionReceiver`: interacción.

## Recursos de paleta
La estrategia compactada conserva un fondo raíz por combinación de paleta/tono y reutiliza superficies comunes, evitando miles de XML repetidos.

## Widgets eliminados
Collage y Overview están retirados del Manifest y del código provider. Las cuatro strings legacy que conservan sus nombres sólo son un seguro de compatibilidad para carpetas antiguas y no hacen que vuelvan al selector.

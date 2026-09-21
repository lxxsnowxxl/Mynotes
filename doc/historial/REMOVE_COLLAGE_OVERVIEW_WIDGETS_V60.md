# MyNotes v60 — eliminación de Collage y Overview

Se eliminaron completamente los widgets **Collage** y **Overview / Resumen**.

## Eliminado
- `CollectionsWidgetProvider.kt` (widget Collage)
- `StatsWidgetProvider.kt` (widget Overview)
- sus layouts `widget_collections.xml` y `widget_stats.xml`
- sus archivos `appwidget-provider`
- sus previews del selector de widgets
- sus receivers del `AndroidManifest.xml`
- sus entradas en `MyNotesWidgetUpdater`
- las cadenas exclusivas de ambos widgets en español, inglés, francés y chino

## Widgets que permanecen
1. Nota rápida
2. Notas recientes
3. Favoritas
4. Nota destacada

No se modificaron adjuntos, miniaturas, previews de enlaces ni el sistema de recordatorios.

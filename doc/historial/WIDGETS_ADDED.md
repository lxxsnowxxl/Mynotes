# Widgets de MyNotes — edición actual

La aplicación incluye **cuatro widgets de pantalla de inicio**, implementados con `RemoteViews` y compatibles con `minSdk 24`.

## 1. Captura rápida

- Diseño horizontal compacto con icono de MyNotes.
- Acento lateral ligado a la paleta seleccionada en Configuración.
- Botón para crear una nota.
- Botón para abrir directamente Buscar notas.

## 2. Notas recientes

- Tarjetas internas, insignias y una franja de color por nota.
- La franja respeta el color individual de la nota; las notas con color `default` usan el acento de la paleta global.
- Muestra de 1 a 5 notas según el alto real del widget.
- Encabezado con total de notas, búsqueda y nueva nota.
- Abre directamente cada nota.

## 3. Favoritas

- Lista independiente de favoritas.
- Se adapta verticalmente de 1 a 4 elementos.
- Permite quitar una nota de Favoritas directamente desde el widget.
- Tocar el encabezado abre el filtro Favoritas de MyNotes.

## 4. Nota destacada

- Selecciona automáticamente la nota más relevante con este orden: fijada → prioridad → favorita → reciente.
- Muestra título, contenido/resumen y metadatos.
- Permite marcar/desmarcar Favorita desde el widget.
- Permite fijar/desfijar desde el widget.
- Botón de apertura directa y botón para crear una nota.

## Widgets retirados

Se eliminaron por completo **Collage** y **Overview/Resumen** del selector de widgets, junto con sus providers, layouts, metadata y previews.

## Integración con MyNotes

- `MainActivity` reconoce acciones de widgets para nueva nota, abrir nota y búsqueda.
- `WidgetActionReceiver` realiza acciones rápidas de favorita/fijada sobre Room.
- `MyNotesWidgetUpdater` refresca únicamente los cuatro widgets activos cuando cambian los datos.
- `SettingsViewModel` actualiza widgets cuando cambia la paleta global o el idioma.
- Crear, editar, borrar, fijar, marcar favorita, cambiar prioridad/categoría o restaurar un backup sigue actualizando los widgets mediante el flujo existente.

## Rendimiento

- `updatePeriodMillis="0"`: no existe polling periódico.
- Room solo se consulta cuando Android solicita actualización o cuando los datos cambian realmente.
- El sistema de adjuntos, miniaturas, previews y `AttachmentPreviewCache` no se modifica.

## Widget picker previews

Los widgets activos conservan sus previews dedicados:
- `widget_preview_quick_note.png`
- `widget_preview_recent_notes.png`
- `widget_preview_favorites.png`
- `widget_preview_focus_note.png`

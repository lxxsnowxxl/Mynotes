# Widgets de MyNotes — edición ampliada

La aplicación incluye ahora **seis widgets de pantalla de inicio**, todos implementados con `RemoteViews` y compatibles con `minSdk 24`.

## 1. Captura rápida

- Diseño horizontal compacto con icono de MyNotes.
- Acento lateral ligado a la paleta seleccionada en Configuración.
- Botón para crear una nota.
- Botón para abrir directamente Buscar notas.

## 2. Notas recientes

- Rediseñado con tarjetas internas, insignias y una franja de color por nota.
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

## 5. Colecciones

Accesos directos a:

- Favoritas
- Fijadas
- Trabajo
- Personal
- Imágenes
- Archivos

También incluye Buscar y Nueva nota.

## 6. Resumen

Panel de métricas con:

- Total de notas
- Favoritas
- Fijadas
- Prioridad alta
- Trabajo
- Personal

Cada bloque abre la colección correspondiente.

## Integración con el código de MyNotes

- `MainActivity` reconoce acciones de widgets para nueva nota, abrir nota, búsqueda y colecciones.
- `NotesScreen` acepta solicitudes externas y añade filtros para **Fijadas** y **Prioridad alta**.
- `WidgetActionReceiver` realiza acciones rápidas de favorita/fijada sobre Room.
- `NoteDao` incluye consultas específicas para recientes, favoritas y nota destacada.
- `MyNotesWidgetUpdater` refresca los seis widgets cuando cambian los datos.
- `SettingsViewModel` actualiza widgets cuando cambia la paleta global o el idioma.
- Crear, editar, borrar, fijar, marcar favorita, cambiar prioridad/categoría o restaurar un backup sigue actualizando los widgets mediante el flujo ya existente.

## Rendimiento

- `updatePeriodMillis="0"`: no existe polling periódico.
- Room solo se consulta cuando Android solicita actualización o cuando los datos cambian realmente.
- No se cargan miniaturas, imágenes adjuntas, video ni previews de enlaces desde los widgets.
- El sistema de `AttachmentPreviewCache` y el renderizado de adjuntos de la app no fueron modificados.

## Widget picker previews

Each widget provider now declares a dedicated `android:previewImage` resource so launchers such as Samsung One UI Home can display a representative thumbnail instead of the generic MyNotes application icon.

Preview assets:
- `widget_preview_quick_note.png`
- `widget_preview_recent_notes.png`
- `widget_preview_favorites.png`
- `widget_preview_focus_note.png`
- `widget_preview_collections.png`
- `widget_preview_stats.png`

The preview artwork matches each widget's real dimensions and visual hierarchy. These files are stored in `res/drawable-nodpi` so Android scales them as artwork rather than applying density-specific bitmap scaling.

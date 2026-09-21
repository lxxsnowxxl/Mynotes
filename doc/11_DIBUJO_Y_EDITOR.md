# Dibujo y editor de notas

## DrawingScreen
Incluye herramientas Fine point/Pencil/Pen, selección de color de trazo, color de lienzo, tamaños, borrador, undo/redo, limpiar y guardado. El lienzo puede ampliarse dentro de la misma pantalla y el panel de herramientas se compacta sin abrir una segunda Activity.

El color de lienzo participa en la exportación final y el borrador se modela de forma que un cambio posterior de fondo no deje manchas del color anterior.

## NoteEditorScreen
Gestiona título/contenido, color de nota y adjuntos pendientes. La paleta se centra/adapta al ancho. Las vistas previas y adjuntos deben mantenerse separadas de optimizaciones generales de UI para no degradar calidad.

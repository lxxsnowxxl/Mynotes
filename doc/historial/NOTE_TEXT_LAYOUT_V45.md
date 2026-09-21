# MyNotes v45 — ajuste del texto de las tarjetas

Se ajustó únicamente el bloque textual y el espaciado interno de `ModernNoteCard`.

## Cambios

- Padding lateral simétrico para que el texto no quede visualmente cargado hacia un lado.
- El título mantiene el tamaño configurado y ahora usa un `lineHeight` explícito (1.22×) para separar mejor las líneas.
- El contenido calcula el `lineHeight` a partir de su tamaño real (`fontSize - 2`) y no del tamaño base mayor. Esto reduce el exceso vertical en tarjetas estrechas.
- Separación entre título y descripción aumentada de 3 dp a 5 dp.
- Título y descripción aprovechan todo el ancho útil del contenedor sin padding final redundante.
- La preview de enlaces y la fila categoría/fecha se alinean con el mismo ancho del texto.

## Adjuntos y miniaturas

No se modificó la implementación de `NoteCardAttachmentsPreview`, `NoteCardAttachmentTile` ni las rutas de caché/decodificación. El bloque de adjuntos de `NoteCard.kt` conserva exactamente el mismo SHA-256 entre v44 y v45:

`b13270b969b96abfe2fefe1188f157d27c4fc44ff4c3bc15b42d6684db5be369`

# 17. Biblioteca de proyectos PDF y persistencia editable

PDF Studio ya no depende únicamente de la sesión en memoria. `PdfProjectRepository` serializa la estructura editable de cada página en JSON y guarda los bitmaps insertados en PNG dentro del almacenamiento privado de MyNotes.

`PdfLibraryActivity` enumera esos proyectos sin cargar sus bitmaps completos, por lo que abrir la biblioteca sigue siendo ligero aunque existan varios documentos. Al elegir un elemento, `PdfEditorActivity` recibe `EXTRA_PROJECT_ID`, reconstruye las páginas, restaura los elementos editables y vuelve a conectar el PDF base interno con el sistema de renderizado por demanda.

El autoguardado se activa mediante una versión de cambios (`dirtyVersion`) y un retraso breve. Los cambios de renderizado temporal de páginas no marcan el proyecto como sucio, evitando escrituras innecesarias al navegar por PDFs largos. Los movimientos y redimensionados sí incrementan la versión y se persisten.

La copia del PDF original se guarda como `source.pdf`. Para que `PdfRenderer` pueda abrirla mediante `ContentResolver`, `file_paths.xml` expone exclusivamente `files/pdf_projects/` a través del `FileProvider` privado ya existente. La Activity nunca exporta ese directorio públicamente.

Los autoguardados actualizan sólo la estructura editable. El PDF final se rasteriza únicamente al pulsar **Exportar**, evitando procesar todas las páginas de un documento grande después de cada cambio.

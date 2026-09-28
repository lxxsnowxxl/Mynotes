# 17 - PDF Studio v65

Esta sección documenta la función **Añadir PDF** incorporada en v65.

## Flujo de usuario
Desde la lista principal se pulsa el FAB `+`. El menú muestra Recordatorios, Nueva nota, Dibujar y Añadir PDF. Al elegir Añadir PDF, `MainActivity` inicia `PdfEditorActivity` mediante un `Intent` explícito. Al ser una Activity separada, el editor tiene su propio ciclo de vida y no modifica el estado de navegación de notas, recordatorios o dibujo.

## PdfEditorActivity
Lee `AppSettings` directamente desde `SettingsRepository` y reconstruye `MyNotesTheme` con la misma paleta, intensidad, tono, color de texto, acento y modo oscuro que la aplicación principal. También configura `UiSoundPlayer`, por lo que las acciones del editor respetan los sonidos y hápticos elegidos en Configuración.

Mantiene una lista inmutable de `PdfPageModel`. Cada página contiene un bitmap de fondo opcional, trazos, textos e imágenes superpuestas. Las posiciones se guardan normalizadas de 0 a 1 para que la misma composición pueda verse en pantalla y exportarse a distintos tamaños de página sin depender de la resolución física del dispositivo.

## PdfDocumentEngine
`loadPdf()` abre un `Uri` con `PdfRenderer`, renderiza cada página a un bitmap y crea un `PdfPageModel` por página. Esto permite anotar un PDF existente sin alterar el archivo original.

`loadImage()` decodifica PNG/JPG/WebP y limita imágenes excesivamente grandes para evitar consumo innecesario de memoria.

`exportPdf()` usa `android.graphics.pdf.PdfDocument`. Para cada página dibuja, en orden: fondo PDF importado, trazos, imágenes y textos. Finalmente escribe el documento al `Uri` entregado por el selector de Android.

## PdfSubjectCropper
Usa Subject Segmentation de ML Kit. `enableForegroundBitmap()` solicita un bitmap donde el sujeto detectado permanece visible y el fondo se vuelve transparente. Después `trimTransparentEdges()` busca el rectángulo mínimo con píxeles visibles y elimina el margen transparente sobrante.

## Herramientas
- Seleccionar: toca una imagen o texto. Las imágenes pueden arrastrarse.
- Dibujar: crea `PdfStroke` con coordenadas normalizadas.
- Texto: tocar la página abre el cuadro de entrada y coloca el texto en esa coordenada.
- Borrador: elimina trazos cercanos al dedo.
- Imagen / PNG: abre el selector de Android e inserta la imagen centrada.
- Abrir PDF: importa un documento existente.
- Nueva página: agrega una página A4 en blanco.
- Recorte IA: elimina fondo y recorta el sujeto seleccionado.
- Girar: rota la imagen 90 grados.
- +/-: cambia el tamaño de la imagen seleccionada.
- Deshacer/Rehacer: conserva hasta 30 estados estructurales del documento.
- Exportar: abre el selector de destino y escribe un PDF real.

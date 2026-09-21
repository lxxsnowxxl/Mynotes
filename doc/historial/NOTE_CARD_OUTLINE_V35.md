# MyNotes v35 — Contorno configurable para tarjetas de notas

## Cambios agregados

Se añadió una nueva personalización para los recuadros de las notas:

- Interruptor para **activar o desactivar el contorno** de las tarjetas.
- Ajuste de **grosor del contorno** mediante slider.
- El color del contorno se resuelve automáticamente en **negro o blanco** según el color del fondo actual para mantener contraste.

## Ubicación en la app

Settings > Advanced > Note cards

Nuevos controles:
- Note outline
- Outline thickness

## Comportamiento

- Si el fondo general es claro, el contorno usa una variante oscura.
- Si el fondo general es oscuro, el contorno usa una variante clara.
- El contorno se aplica a las tarjetas de notas sin alterar la visualización de adjuntos ni miniaturas.
- Los valores quedan guardados en DataStore y también se incluyen en Backup & Restore.

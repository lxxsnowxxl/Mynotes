# MyNotes v39 — Corrección de contraste de texto

## Cambios

- Los modos manuales Black y White ahora conservan el color elegido siempre que mantenga al menos 4.5:1 de contraste con la superficie real.
- Si el color manual queda ilegible, MyNotes selecciona automáticamente negro o blanco únicamente para esa superficie.
- La regla de tonos de la paleta global sigue vigente, pero ahora tiene un fallback de contraste real.
- La pantalla principal resuelve explícitamente el color de "My notes" y su descripción contra el fondo actual.
- Los botones secundarios del menú + calculan su texto contra el color real de la píldora.
- Settings separa el color de textos del panel interior del color de encabezados que están directamente sobre el fondo general.
- Se corrigieron Extreme customization, Options menu y Reset menu.
- New note / Edit note ahora usan `noteUiTextColor` para título, campos, descripciones, paleta y elementos gráficos del editor.

## Adjuntos y miniaturas

No se modificaron los archivos protegidos de carga, caché, renderizado o previews de adjuntos y miniaturas. Sus SHA-256 siguen coincidiendo con v38.

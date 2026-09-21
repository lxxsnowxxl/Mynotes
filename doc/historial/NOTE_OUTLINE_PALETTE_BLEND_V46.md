# MyNotes v46 — Borde de notas combinado con la paleta

## Cambio
El contorno de las notas ya no usa sólo negro o blanco.

Ahora el borde se calcula a partir del color real de fondo de cada nota:
- si el fondo es claro, el borde toma un tono más oscuro de la misma familia
- si el fondo es oscuro, el borde toma un tono más claro de la misma familia
- si una combinación extrema no alcanza contraste suficiente, se usa negro/blanco automático como respaldo

## Resultado
- el borde se ve más integrado con cada paleta
- se conserva la diferenciación visual entre tonos claros y oscuros
- el slider de grosor sigue funcionando igual

## Archivos modificados
- `app/src/main/java/com/example/mynotes/ui/theme/NoteColors.kt`
- `app/src/main/java/com/example/mynotes/ui/components/NoteCard.kt`

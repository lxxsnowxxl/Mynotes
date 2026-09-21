# MyNotes v57 — Notificación de recordatorio en pantalla de bloqueo

## Problema
En Samsung/Android 9 la vista compacta personalizada de la notificación dispone de menos altura en la pantalla de bloqueo que en la cortina de notificaciones. Además, el código sobrescribía los tamaños pequeños del XML y podía llevar el título a 17 sp y la descripción a 13 sp.

## Cambios
- vista compacta: 48 dp → 38 dp
- padding exterior: 2 dp → 0 dp
- icono y franja de acento más compactos
- márgenes horizontales ajustados
- título compacto limitado a 12–14 sp
- descripción compacta limitada a 8–10 sp
- heads-up conserva un rango intermedio propio
- vista expandida conserva los tamaños anteriores

El diseño sigue usando el fondo, acento y contraste configurados en MyNotes.

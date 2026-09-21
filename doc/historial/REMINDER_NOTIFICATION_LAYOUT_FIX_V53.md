# MyNotes v53 — Corrección de recorte en notificaciones

La notificación personalizada de Recordatorios usaba una vista compacta de 88 dp. En Samsung/Android 9 el sistema reserva menos altura para una notificación personalizada decorada, por lo que el contenido se recortaba.

## Cambios
- Vista compacta independiente de 48 dp.
- Vista heads-up independiente de 72 dp.
- Vista expandida ajustada a 148 dp.
- La vista compacta muestra título + una línea de descripción; el encabezado del sistema ya muestra MyNotes.
- Heads-up muestra MyNotes · Reminders, título y descripción.
- La vista expandida mantiene título, descripción, fecha/hora, repetición y prioridad.
- Se limita el escalado tipográfico dentro de notificaciones para impedir que un tamaño de fuente alto rompa la altura disponible.
- Se conserva paleta, acento, contraste, sonido y vibración configurados en MyNotes.

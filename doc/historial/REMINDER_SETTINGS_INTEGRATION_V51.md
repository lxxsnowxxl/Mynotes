# MyNotes v51 — Recordatorios vinculados a Configuración

La pantalla y los avisos de Recordatorios ahora reutilizan la configuración global de MyNotes.

## Apariencia
- Paleta global y tono de fondo mediante `MaterialTheme`.
- Color de texto `Auto / Black / White` con corrección de contraste.
- Fuente seleccionada y tamaño global de texto.
- Color de acento para botones, chips, switches y el nuevo color `palette` de cada recordatorio.
- Intensidad de paneles, modo claro/oscuro y elevación.
- Radio, padding y elevación de tarjetas según la personalización existente de tarjetas.
- Tamaño global de iconos.
- Animaciones, estilo, easing, velocidad, intensidad y perfil de rendimiento al pasar lista ↔ editor.

## Sonido y vibración dentro de Recordatorios
Las acciones de la pantalla usan `UiSoundPlayer`, por lo que respetan:
- efectos de sonido activados/desactivados
- volumen
- paquete de sonidos
- vibración activada/desactivada
- intensidad
- estilo háptico

Se aplicó feedback a volver, abrir/editar, guardar, borrar, fecha/hora, repetición, prioridad, color e interruptores.

## Aviso cuando se dispara el recordatorio
Se añadió `ReminderFeedbackPreferences`, que mantiene una copia síncrona de las preferencias de audio/hápticos para el `BroadcastReceiver`.

Cuando llega un recordatorio:
- el canal de Android queda silencioso para evitar doble sonido/doble vibración
- MyNotes reproduce el sonido del paquete configurado con el volumen elegido
- MyNotes reproduce el patrón háptico con estilo e intensidad configurados

## Paleta del recordatorio
Los recordatorios nuevos usan `palette` por defecto. Ese color sigue el acento de la paleta seleccionada; los colores manuales anteriores continúan disponibles y los recordatorios existentes son compatibles.

## Archivos principales modificados
- `MainActivity.kt`
- `ReminderScreen.kt`
- `Reminder.kt`
- `ReminderRepository.kt`
- `ReminderReceiver.kt`
- `ReminderFeedbackPreferences.kt` (nuevo)

No se modificaron los módulos de adjuntos, miniaturas, previews ni sus cachés.

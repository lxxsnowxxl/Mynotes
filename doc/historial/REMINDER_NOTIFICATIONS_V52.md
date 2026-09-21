# MyNotes v52 — Recordatorios en notificaciones con diseño de la app

## Notificaciones reales
Los recordatorios programados se muestran mediante `NotificationManager` cuando llega su fecha y hora, incluso si MyNotes no está abierta, siempre que Android permita las notificaciones de la app.

## Diseño integrado con MyNotes
Se añadieron vistas personalizadas para la notificación compacta, expandida y emergente (heads-up):
- fondo basado en el panel de la paleta actual
- color de texto con el mismo sistema de contraste de MyNotes
- acento basado en la paleta/acento configurado
- si el recordatorio tiene un color propio, ese color se utiliza como acento
- tamaño de texto vinculado al tamaño configurado en MyNotes
- tipografía visual Google Sans en las vistas de notificación
- icono y franja lateral de acento

La versión expandida muestra fecha/hora, repetición y prioridad.

## Configuración
`ReminderFeedbackPreferences` guarda una instantánea síncrona de los colores de la interfaz además de sonido y vibración. Esto permite a `ReminderReceiver` crear la notificación desde un `BroadcastReceiver` sin bloquear esperando DataStore.

La instantánea visual se actualiza cuando cambian la paleta, tono, intensidad, color de texto, acento, tamaño de fuente o modo claro/oscuro.

## Compatibilidad
- API 28: usa `RemoteViews` personalizadas y `DecoratedCustomViewStyle`.
- Android 13+: respeta `POST_NOTIFICATIONS`.
- El canal se mantiene silencioso porque sonido y vibración siguen siendo reproducidos por MyNotes según Configuración, evitando feedback duplicado.

## Archivos principales modificados
- `ReminderReceiver.kt`
- `ReminderFeedbackPreferences.kt`
- `MainActivity.kt`
- `Theme.kt`
- `res/layout/notification_reminder_compact.xml`
- `res/layout/notification_reminder_expanded.xml`
- `res/drawable/reminder_notification_panel.xml`
- `res/drawable/reminder_notification_accent.xml`

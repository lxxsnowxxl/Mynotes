# Recordatorios y notificaciones

## Componentes
- `Reminder.kt`: modelo.
- `ReminderRepository.kt`: persistencia/CRUD.
- `ReminderAlarmScheduler.kt`: AlarmManager y cálculo de próximas ejecuciones.
- `ReminderReceiver.kt`: recepción de alarmas, boot/package replaced y publicación de notificación.
- `ReminderFeedbackPreferences.kt`: adapta apariencia/sonido/vibración a Settings.
- `ReminderScreen.kt`: lista + editor.

## Flujo
1. El usuario crea/edita desde ReminderScreen.
2. Repository persiste.
3. Scheduler programa la próxima fecha válida, incluida programación a pocos minutos.
4. Receiver recibe el PendingIntent y crea la notificación.
5. En repetición, se calcula/programa la siguiente ocurrencia.
6. Tras BOOT_COMPLETED/MY_PACKAGE_REPLACED se reprograman recordatorios activos.

## Notificación
La vista colapsada usa plantilla nativa para evitar recortes de One UI/API 28. Los estados heads-up/expandido pueden usar presentación personalizada. El canal evita duplicar feedback cuando MyNotes reproduce sus propios efectos configurados.

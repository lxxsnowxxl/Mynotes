# MyNotes v49 — Recordatorios

## Menú +
El speed-dial principal ahora tiene tres acciones:
- Nueva nota
- Dibujar
- Recordatorios

## Pantalla de recordatorios
Incluye una lista persistente de recordatorios con:
- activar/desactivar rápidamente
- fecha y hora
- repetición
- edición al tocar la tarjeta
- color identificador

## Editor de recordatorios
El editor permite configurar:
- título
- descripción
- fecha
- hora
- repetición: ninguna, diaria, lunes-viernes, semanal o mensual
- prioridad: baja, normal o alta
- color
- recordatorio activo/inactivo
- eliminar recordatorios existentes

## Notificaciones
- Android 7–11: alarma exacta mediante `setExactAndAllowWhileIdle`.
- Android 12+: alarma compatible mediante `setAndAllowWhileIdle` sin obligar al usuario a conceder acceso especial de alarmas exactas.
- Android 13+: MyNotes solicita permiso de notificaciones cuando se guarda un recordatorio activo.
- Los recordatorios activos se vuelven a programar después de reiniciar o actualizar la aplicación.
- Al tocar una notificación se abre la sección Recordatorios.

## Persistencia
Los recordatorios se almacenan de forma independiente en SharedPreferences/JSON. No se cambió la base Room de notas y adjuntos, por lo que no se introduce una migración de base de datos.

## Idiomas
Se añadieron textos para:
- Español
- Inglés
- Francés
- Chino simplificado

## Archivos principales nuevos
- `reminders/Reminder.kt`
- `reminders/ReminderRepository.kt`
- `reminders/ReminderAlarmScheduler.kt`
- `reminders/ReminderReceiver.kt`
- `ui/theme/ReminderScreen.kt`
- `res/values*/strings_reminders.xml`
- `res/drawable/ic_reminder_notification.xml`

## Compatibilidad
No se modificaron las rutas de renderizado/caché de adjuntos, miniaturas o previews.

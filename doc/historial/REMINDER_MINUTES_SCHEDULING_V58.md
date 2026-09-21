# MyNotes v58 — Recordatorios a pocos minutos

## Cambios
- Los recordatorios nuevos se inicializan a +5 minutos en vez de +1 hora.
- Si se edita un recordatorio vencido, el editor propone +5 minutos desde el momento actual.
- Se agregaron accesos rápidos +5, +10, +15, +30 y +60 min.
- Los accesos rápidos actualizan fecha y hora desde el instante actual, por lo que no es necesario saltar al día siguiente.
- En Android 7–11 se mantiene `AlarmManager.setExactAndAllowWhileIdle`, por lo que en el Samsung API 28 la alarma puede programarse con precisión de minutos.

## Archivos modificados
- `ui/theme/ReminderScreen.kt`
- `values*/strings_reminders.xml`

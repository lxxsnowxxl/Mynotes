# MyNotes v59 — notificación colapsada compatible con Samsung

## Problema
Samsung/One UI en Android 9 limita con mucha agresividad la altura de los `RemoteViews` personalizados cuando la notificación está colapsada. El bloque de MyNotes podía verse recortado hasta que el usuario expandía la notificación.

## Solución
- La vista **colapsada** ahora usa la plantilla nativa de Android.
- Se muestran directamente el título y la descripción del recordatorio dentro del alto que el sistema garantiza.
- Se conserva el color de acento de MyNotes mediante `NotificationCompat.Builder.setColor()`.
- La vista **heads-up** sigue siendo personalizada con el diseño de MyNotes.
- La vista **expandida** sigue siendo personalizada y muestra fecha, repetición y prioridad.
- Se eliminó `notification_reminder_compact.xml`, ya que el sistema ya no renderiza un layout personalizado en el estado colapsado.

## Motivo
Una app no puede obligar a One UI a asignar más altura a una notificación colapsada. Usar el template nativo evita el recorte porque Android calcula internamente el espacio disponible.

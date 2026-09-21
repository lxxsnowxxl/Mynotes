# Cambios recientes integrados

Esta revisión documental ya incluye la evolución acumulada del proyecto hasta v61.

## Tarjetas y apariencia
- Contorno de notas configurable mediante grosor, con 0 dp como apagado.
- Espaciado interno adaptativo cuando el contorno es grueso.
- El borde deriva del color real de la tarjeta y se aclara/oscurece para mantener contraste.
- Correcciones de contraste de texto en fondos claros/oscuros y ajuste de layout de título/contenido.

## Enlaces y previews
- El enlace visible puede retirarse del cuerpo cuando la preview se resuelve, conservándose internamente el dato necesario para la tarjeta.
- LinkPreviewRepository y LinkPreviewCard forman ahora un subsistema explícito documentado.

## Dibujo
- Pantalla propia con herramientas, colores, tamaño, borrador, undo/redo, color de lienzo y expansión del área sin abrir otra Activity.

## Recordatorios
- Lista/editor, persistencia, repetición, prioridad, color, activación y programación en minutos.
- AlarmManager + BroadcastReceiver para disparo y reprogramación tras reinicio/actualización.
- Notificaciones con vista nativa colapsada para compatibilidad Samsung/API 28 y vistas personalizadas para estados ampliados.
- Sonido/vibración y apariencia conectados con AppSettings.

## Widgets
- Widgets sincronizados con el idioma interno de MyNotes.
- Recursos de paleta compactados para evitar miles de drawables.
- Collage y Overview fueron retirados del Manifest y providers; quedan cuatro widgets activos.
- v61 mantiene cuatro strings legacy sólo para tolerar XML residuales de instalaciones/carpetas antiguas; no reactivan providers.

## Rendimiento
- Se preserva la estrategia de caché/miniaturas, evitando modificar rutas críticas de adjuntos al realizar optimizaciones generales.

# MyNotes v36 — enlaces convertidos en previews

Al pegar un enlace HTTP/HTTPS en una nota, MyNotes genera la tarjeta enriquecida con imagen, título y descripción.

Cuando la preview se carga correctamente:
- la URL desaparece del campo de texto del editor;
- se puede seguir escribiendo normalmente;
- la URL se conserva como metadato interno para reconstruir la preview después de guardar;
- la URL deja de mostrarse en las tarjetas de notas, detalle y widgets;
- las notas antiguas también dejan de repetir el enlace como texto visible.

La implementación no añade columnas nuevas a Room.

# MyNotes v40 — Espaciado adaptativo para contornos gruesos

## Problema corregido
Cuando el contorno de una tarjeta de nota estaba configurado cerca de 5–6 dp, el borde ocupaba demasiado espacio visual hacia el interior. Esto hacía que títulos, iconos, categoría y fecha quedaran demasiado próximos al contorno.

## Cambio
- El contenido textual de la tarjeta ahora reserva un margen adicional proporcional al grosor del contorno.
- El ajuste se aplica a laterales, parte superior y especialmente parte inferior.
- La fila de categoría/fecha recibe un pequeño margen adicional cuando el borde es grueso.
- Con contornos finos, la diferencia de tamaño es prácticamente imperceptible.
- Con el máximo de 6 dp, el contenido se separa aproximadamente 5.4 dp adicionales del borde.

## Adjuntos y miniaturas
No se cambió la composición ni el código de carga/renderizado de adjuntos o miniaturas. El ajuste ocurre únicamente en el bloque interno de texto y controles de la tarjeta.

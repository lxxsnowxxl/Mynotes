# MyNotes v38 — Contorno simplificado y contraste adaptativo

## Interfaz

Se eliminó el interruptor visible **Note outline**.

Ahora existe un solo control:

- **Outline thickness**: 0.0 dp a 6.0 dp.
- `0.0 dp` equivale a contorno apagado.
- Cualquier valor superior a cero activa el contorno automáticamente.

La preferencia booleana anterior se conserva internamente solo para migrar configuraciones/respaldos antiguos sin perder compatibilidad.

## Contraste

El borde ya no decide negro/blanco usando el fondo general de la pantalla.
Ahora se calcula contra el **color real de cada tarjeta de nota** mediante `automaticUiTextColor()`.

Esta función compara las relaciones de contraste WCAG de negro y blanco y selecciona la alternativa de mayor contraste.
El borde es opaco para no perder contraste al cambiar de paleta.

### Verificación del catálogo

Se revisaron los 46 grupos de `PaletteCatalog`, con 4 tonos por paleta (184 tonos de fondo).
También se incluyeron los 46 accents en la comprobación ampliada (230 colores en total).

- Peor relación de contraste usando la elección automática negro/blanco: **4.68:1** aproximadamente.
- El criterio habitual para componentes gráficos y límites visuales es 3:1.

Los colores individuales de notas también quedan cubiertos porque el cálculo se realiza en tiempo de composición contra la tarjeta real, no contra una tabla fija.

## Adjuntos y miniaturas

No se modificaron los módulos de carga, caché, decodificación o renderizado de adjuntos/miniaturas.
`NoteCard.kt` solo cambió en la resolución del color del borde; el bloque de adjuntos permanece sin cambios.

Los hashes de los demás archivos protegidos se encuentran en `ATTACHMENTS_THUMBNAILS_SHA256_V38.txt`.

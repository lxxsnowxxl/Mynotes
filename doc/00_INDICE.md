# Documentación de MyNotes

El código de `app/` es la referencia de comportamiento de la aplicación. La
documentación reúne explicaciones históricas y notas de cambios posteriores;
sus antiguos conteos, hashes y números de línea no representan una auditoría del
árbol actual.

## Diseños y personalización

- [Layouts XML y relación con Compose](LAYOUTS_XML_V268.md).
- [Mapa de pantallas y fuentes Compose](layout_compose_previews.json).
- [Fondo global](FONDO_GLOBAL_V270.md).
- [Degradado y blur en los cinco tonos](DEGRADADO_BLUR_5_TONOS_V279.md).
- [Revisión de rendimiento del perfil y fondos](REVISION_RENDIMIENTO_V276.md).
- [Transiciones nuevas](../CAMBIOS_v287_NUEVAS_TRANSICIONES.md).
- [Botones compactos de notas](../CAMBIOS_v288_BOTONES_COMPACTOS.md).
- [Blur animado](../CAMBIOS_v291_BLUR_ANIMADO.md).
- [Corrección del movimiento del blur](../CAMBIOS_v292_CORRECCION_BLUR_ANIMADO.md).
- [Comprobaciones de la corrección del blur](../VALIDACION_v292.json).
- [Diseño clásico del selector con cuatro fondos](../CAMBIOS_v293_DISENO_FONDOS.md).

## Explicaciones del código

- [Explicaciones por archivo en Markdown](archivos/): 72 documentos de la
  revisión histórica v63.
- [Explicaciones individuales ultra detalladas](explicacion_ultra_detallada/):
  72 documentos conservados íntegramente.
- [Flujos ultra detallados](flujos_ultra_detallados/).
- [Recursos Android explicados](RECURSOS_ANDROID_ULTRA_DETALLADOS.txt).
- [Mapa de arquitectura histórico](06_MAPA_ARQUITECTURA_ACTUAL.md).
- [Notas históricas](historial/).

Los TXT equivalentes y los agregados que repetían esos documentos se retiraron
en v289. El contenido de las explicaciones individuales se conserva en las
rutas indicadas arriba.

## Limpieza y comprobaciones

- [Resumen de limpieza](../LIMPIEZA_v289.md).
- [Inventario de archivos retirados, motivos y huellas SHA-256](limpieza_v289.json).
- [Validación de la limpieza v289](../VALIDACION_v289.json).
- [Rendimiento y versión 3.2.5](../CAMBIOS_v290_RENDIMIENTO_3_2_5.md).
- [Validación de la revisión de rendimiento](../VALIDACION_v290.json).
- [Validación del blur animado](../VALIDACION_v291.json).

Las herramientas de `tools/` comprueban estructuras, recursos y contratos de
fuentes. Sus resultados estáticos no sustituyen compilar ni probar en Android.

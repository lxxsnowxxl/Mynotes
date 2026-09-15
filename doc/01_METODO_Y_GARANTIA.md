# Método y garantía de no modificación — actualización 2026-09-15

## Regla aplicada

La documentación se actualiza **sin insertar comentarios, renombrar símbolos, reordenar imports ni reformatear `app/`**. Todo el material nuevo vive bajo `DOCUMENTACION_CODIGO_OBSESIVA/`.

## Estado actual

- Kotlin/Kotlin DSL documentado: **48 archivos**.
- Fuentes cambiados desde la instantánea documental anterior y re-documentados: **9**.
- Fuente nuevo incorporado: **1**.
- Código modificado por esta tarea documental: **0 archivos**.

## Prueba byte a byte

Se calculó SHA-256 de todos los `.kt/.kts` antes de regenerar la documentación y se vuelve a verificar al finalizar. La comparación es binaria: espacios, comentarios y saltos de línea también cuentan. Ver `02_SHA256_CODIGO_INTACTO.txt`.

## Qué se explica

Cada documento actualizado cubre package/imports, declaraciones, parámetros, callbacks, nulabilidad, mutabilidad, variables, estado Compose, límites `coerce*`, bloques de control, efectos, corrutinas, caché, I/O, lifecycle, side effects y relaciones con los cambios recientes. Cuando la intención humana no puede demostrarse desde el código, se describe el comportamiento observable en lugar de inventar motivos.

## Relación documento ↔ código

El código es la fuente de verdad. Si el documento y el fuente discrepan en el futuro, debe actualizarse la documentación; **nunca debe alterarse el código únicamente para hacerlo coincidir con el texto explicativo**.

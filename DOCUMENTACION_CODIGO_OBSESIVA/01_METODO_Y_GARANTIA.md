# Método y garantía de no modificación

## Regla aplicada

El usuario pidió explicaciones extremadamente detalladas **sin cambiar el código ya establecido**. Por eso no se insertaron comentarios en `app/src/...` ni en `app/build.gradle.kts`. Toda la explicación vive fuera del árbol de fuentes compilados, en `DOCUMENTACION_CODIGO_OBSESIVA`.

## Comprobación binaria de los fuentes

Antes de generar la documentación se calculó SHA-256 de cada `.kt/.kts`. Al terminar se volvió a calcular. Todos deben coincidir exactamente byte por byte; no se ignoran espacios ni comentarios en esta prueba. Si cambia incluso un salto de línea, el hash cambia.

## Alcance de la explicación

La extracción es estática y se apoya en el código existente. Se describen contratos, mutabilidad, nulabilidad, callbacks, side effects, restricciones, bloques de control y semántica Android/Compose. Cuando una intención no puede deducirse de manera inequívoca solo por el código, el documento evita afirmar una intención inventada y describe el comportamiento observable de la construcción.

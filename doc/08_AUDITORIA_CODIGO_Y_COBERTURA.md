# Auditoría de código y cobertura — v63

> Mantenimiento v289: este documento describe la revisión histórica v63. Las cifras no representan la cobertura actual. La explicación se conserva en Markdown en [archivos/](archivos/); se retiraron los TXT equivalentes y el agregado repetido. Consulta el [índice actual](00_INDICE.md).

## Qué se corrigió respecto de v62

v62 tenía cobertura nominal 72/72, pero varios documentos eran demasiado superficiales: enumeraban imports/declaraciones y no explicaban el funcionamiento interno. v63 regenera cada documento con explicación por función, entradas/salidas, efectos laterales, APIs usadas, integración y precauciones.

## Cobertura cuantitativa

- Fuentes Kotlin/KTS: **72**.
- Líneas de código fuente: **23794**.
- Documentos Markdown individuales: **72**.
- Documentos TXT individuales: **72**.
- Líneas de explicación Markdown generadas: **17152** (promedio 238.2 por fuente).
- Documento maestro TXT: `EXPLICACION_COMPLETA_DEL_CODIGO.txt`.
- Cobertura de fuentes: **100%**.

## Qué contiene ahora cada explicación

1. Responsabilidad real del archivo.
2. Tipos/clases declarados.
3. Estado, constantes y valores importantes.
4. Funciones una por una, con líneas, firma, entradas, salida y explicación.
5. Efectos observados: Room, DataStore, red, archivos, alarmas, notificaciones, RemoteViews, bitmap, audio/háptica, etc.
6. Dependencias internas de MyNotes.
7. Recursos Android `R.*`.
8. Lectura práctica del flujo.
9. Riesgos/contratos que deben conservarse al editar.
10. Resumen en lenguaje sencillo.

## Integridad del código

La generación modifica únicamente `doc/`. El árbol `app/` se copió sin cambios y sus SHA-256 se vuelven a registrar en `02_SHA256_CODIGO_INTACTO.txt`.

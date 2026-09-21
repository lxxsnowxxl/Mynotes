# MyNotes v61 — corrección de recursos residuales de widgets

El build fallaba porque una carpeta antigua todavía contenía `widget_collections.xml` y `widget_stats.xml`, aunque esos archivos ya no forman parte del proyecto limpio v60.

## Corrección
- Collage y Overview siguen eliminados del `AndroidManifest` y no aparecen como widgets.
- Se restauran cuatro cadenas **solo como compatibilidad** para que un proyecto viejo con XML residuales no falle durante `processDebugResources`.
- Se incluye `CLEAN_OLD_WIDGET_RESOURCES.bat` para borrar físicamente los XML antiguos al trabajar sobre una carpeta previamente usada.

## Recomendación
Lo más limpio es extraer el ZIP en una carpeta nueva. Si vas a reemplazar archivos sobre el proyecto anterior, ejecuta `CLEAN_OLD_WIDGET_RESOURCES.bat` una vez.

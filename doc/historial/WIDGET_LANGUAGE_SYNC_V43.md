# MyNotes v43 — Widgets sincronizados con el idioma de la app

## Problema

Las traducciones de los widgets sí existían, pero `RemoteViews` se inflaba desde el launcher con la configuración de idioma del sistema. Por eso cambiar el idioma dentro de MyNotes no cambiaba necesariamente el texto ya mostrado en los widgets.

## Corrección

- Se añadió `WidgetLocale.kt`, que lee la misma preferencia `locale_prefs/language` usada por `MainActivity`.
- Cada proveedor de widget crea un contexto localizado con el idioma seleccionado en MyNotes.
- Los textos visibles se escriben explícitamente con `RemoteViews.setTextViewText(...)` en vez de depender del `android:text` estático del layout.
- También se localizan los textos dinámicos: categorías, prioridades, estados, contadores y fechas.
- Los plurales de Recent/Favorites se resuelven con el contexto localizado.
- Las descripciones de accesibilidad de los botones +, búsqueda, favorito y pin también usan el idioma de MyNotes.
- Al cambiar el idioma, `MainActivity` solicita inmediatamente un refresco de todos los widgets antes de recrearse.

## Widgets cubiertos

- Quick capture
- Recent notes
- Favorites
- Focus note
- Collage
- Overview

## Idiomas comprobados

- Español
- English
- Français
- 中文（简体）

Se validó que las claves usadas por los widgets existen en todos esos recursos y que los XML del proyecto siguen siendo válidos.

## Nota sobre el selector del launcher

El contenido de los widgets colocados sí puede seguir el idioma interno de MyNotes con esta solución. El nombre y la miniatura mostrados por algunos launchers en el selector de widgets pueden seguir el idioma del sistema porque los renderiza el propio launcher fuera del proceso de MyNotes.

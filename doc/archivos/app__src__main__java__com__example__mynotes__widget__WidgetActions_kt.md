# WidgetActions.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/widget/WidgetActions.kt`  
**SHA-256:** `e96b5d6ae6fda8cb2424a6db5efa40f9cbe7bc4dab4de8d49450eb051c995b2f`  
**Líneas:** 23  
**Package:** `com.example.mynotes.widget`

## 1. Para qué existe este archivo

Constantes de acciones/extras/colecciones usadas para comunicar widgets con MainActivity.

## 2. Tipos/clases declarados

- Línea **3** — `object WidgetActions`.

## 3. Estado, constantes y valores importantes

- **`ACTION_NEW_NOTE`** (línea 4) inicia con `"com.example.mynotes.widget.NEW_NOTE"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`ACTION_OPEN_NOTE`** (línea 5) inicia con `"com.example.mynotes.widget.OPEN_NOTE"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`ACTION_OPEN_COLLECTION`** (línea 6) inicia con `"com.example.mynotes.widget.OPEN_COLLECTION"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`ACTION_SEARCH`** (línea 7) inicia con `"com.example.mynotes.widget.SEARCH"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`ACTION_TOGGLE_FAVORITE`** (línea 8) inicia con `"com.example.mynotes.widget.TOGGLE_FAVORITE"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`ACTION_TOGGLE_PIN`** (línea 9) inicia con `"com.example.mynotes.widget.TOGGLE_PIN"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`EXTRA_NOTE_ID`** (línea 10) inicia con `"widget_note_id"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`EXTRA_COLLECTION`** (línea 12) inicia con `"widget_collection"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`COLLECTION_ALL`** (línea 13) inicia con `"all"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`COLLECTION_FAVORITES`** (línea 15) inicia con `"favorites"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`COLLECTION_PINNED`** (línea 16) inicia con `"pinned"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`COLLECTION_PRIORITY`** (línea 17) inicia con `"priority"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`COLLECTION_WORK`** (línea 18) inicia con `"work"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`COLLECTION_PERSONAL`** (línea 19) inicia con `"personal"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`COLLECTION_IMAGES`** (línea 20) inicia con `"images"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`COLLECTION_FILES`** (línea 21) inicia con `"files"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

Este archivo no declara funciones.

## 5. Cómo se conecta con el resto de MyNotes

- No importa directamente otro componente `com.example.mynotes`; funciona como modelo/utilidad base o mediante APIs Android/Jetpack.

## 6. Recursos Android que utiliza

- No se detectaron referencias directas `R.*` en este archivo.

## 7. Tecnologías y efectos relevantes

- Principalmente lógica Kotlin/Compose sin I/O especial detectado por estas reglas.

## 8. Lectura práctica del flujo

No hay flujo ejecutable propio; su contenido sirve de declaración/configuración para otros archivos.

## 9. Qué no debe romperse al modificarlo

- Mantener las firmas públicas/callbacks que usan los archivos listados en la sección de integración.

## 10. Resumen en lenguaje sencillo

En términos simples: Constantes de acciones/extras/colecciones usadas para comunicar widgets con MainActivity. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.

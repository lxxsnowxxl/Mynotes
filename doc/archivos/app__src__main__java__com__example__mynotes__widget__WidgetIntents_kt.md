# WidgetIntents.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/widget/WidgetIntents.kt`  
**SHA-256:** `6699c32c4694794462bbea7ee6c9367655e8c1a09c05cbb0b1b487c0560b58bc`  
**Líneas:** 79  
**Package:** `com.example.mynotes.widget`

## 1. Para qué existe este archivo

Fábrica de PendingIntent para abrir app, crear nota, abrir nota/colección, buscar o alternar estados.

## 2. Tipos/clases declarados

- Línea **9** — `object WidgetIntents`.

## 3. Estado, constantes y valores importantes

- **`ACTIVITY_FLAGS`** (línea 10) inicia con `Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`PENDING_FLAGS`** (línea 11) inicia con `PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`intent`** (línea 14) inicia con `Intent(context`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `openApp` — líneas 13–21

**Firma:** `fun openApp(context: Context, uniqueId: Int): PendingIntent`

Construye/ejecuta la operación necesaria para abrir el destino indicado, aplicando las validaciones visibles en el cuerpo.

**Entradas:**
- `context: Context`
- `uniqueId: Int`

**Salida:** PendingIntent.

**Efectos/APIs observados en el cuerpo:**
- Crea un PendingIntent para una acción futura del sistema.
- Inicia o prepara navegación/acción mediante Intent.

**Operaciones/funciones que coordina:** `Intent`, `addCategory`, `parse`, `getActivity`.

### `newNote` — líneas 23–30

**Firma:** `fun newNote(context: Context, uniqueId: Int): PendingIntent`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `uniqueId: Int`

**Salida:** PendingIntent.

**Efectos/APIs observados en el cuerpo:**
- Crea un PendingIntent para una acción futura del sistema.
- Inicia o prepara navegación/acción mediante Intent.

**Operaciones/funciones que coordina:** `Intent`, `parse`, `getActivity`.

### `openNote` — líneas 32–40

**Firma:** `fun openNote(context: Context, noteId: Int, uniqueId: Int): PendingIntent`

Construye/ejecuta la operación necesaria para abrir el destino indicado, aplicando las validaciones visibles en el cuerpo.

**Entradas:**
- `context: Context`
- `noteId: Int`
- `uniqueId: Int`

**Salida:** PendingIntent.

**Efectos/APIs observados en el cuerpo:**
- Crea un PendingIntent para una acción futura del sistema.
- Inicia o prepara navegación/acción mediante Intent.

**Operaciones/funciones que coordina:** `Intent`, `parse`, `putExtra`, `getActivity`.

### `openCollection` — líneas 42–50

**Firma:** `fun openCollection(context: Context, collection: String, uniqueId: Int): PendingIntent`

Construye/ejecuta la operación necesaria para abrir el destino indicado, aplicando las validaciones visibles en el cuerpo.

**Entradas:**
- `context: Context`
- `collection: String`
- `uniqueId: Int`

**Salida:** PendingIntent.

**Efectos/APIs observados en el cuerpo:**
- Crea un PendingIntent para una acción futura del sistema.
- Inicia o prepara navegación/acción mediante Intent.

**Operaciones/funciones que coordina:** `Intent`, `parse`, `putExtra`, `getActivity`.

### `search` — líneas 52–59

**Firma:** `fun search(context: Context, uniqueId: Int): PendingIntent`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `uniqueId: Int`

**Salida:** PendingIntent.

**Efectos/APIs observados en el cuerpo:**
- Crea un PendingIntent para una acción futura del sistema.
- Inicia o prepara navegación/acción mediante Intent.

**Operaciones/funciones que coordina:** `Intent`, `parse`, `getActivity`.

### `toggleFavorite` — líneas 61–68

**Firma:** `fun toggleFavorite(context: Context, noteId: Int, uniqueId: Int): PendingIntent`

Invierte el estado booleano asociado al elemento y propaga el cambio a la capa persistente; después actualiza las superficies que dependen de ese estado cuando el cuerpo lo solicita.

**Entradas:**
- `context: Context`
- `noteId: Int`
- `uniqueId: Int`

**Salida:** PendingIntent.

**Efectos/APIs observados en el cuerpo:**
- Crea un PendingIntent para una acción futura del sistema.
- Inicia o prepara navegación/acción mediante Intent.

**Operaciones/funciones que coordina:** `Intent`, `parse`, `putExtra`, `getBroadcast`.

### `togglePin` — líneas 70–77

**Firma:** `fun togglePin(context: Context, noteId: Int, uniqueId: Int): PendingIntent`

Invierte el estado booleano asociado al elemento y propaga el cambio a la capa persistente; después actualiza las superficies que dependen de ese estado cuando el cuerpo lo solicita.

**Entradas:**
- `context: Context`
- `noteId: Int`
- `uniqueId: Int`

**Salida:** PendingIntent.

**Efectos/APIs observados en el cuerpo:**
- Crea un PendingIntent para una acción futura del sistema.
- Inicia o prepara navegación/acción mediante Intent.

**Operaciones/funciones que coordina:** `Intent`, `parse`, `putExtra`, `getBroadcast`.

## 5. Cómo se conecta con el resto de MyNotes

- Usa `com.example.mynotes.MainActivity`.

## 6. Recursos Android que utiliza

- No se detectaron referencias directas `R.*` en este archivo.

## 7. Tecnologías y efectos relevantes

- Crea un PendingIntent para una acción futura del sistema.
- Inicia o prepara navegación/acción mediante Intent.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `openApp` — Construye/ejecuta la operación necesaria para abrir el destino indicado, aplicando las validaciones visibles en el cuerpo.
2. `newNote` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
3. `openNote` — Construye/ejecuta la operación necesaria para abrir el destino indicado, aplicando las validaciones visibles en el cuerpo.
4. `openCollection` — Construye/ejecuta la operación necesaria para abrir el destino indicado, aplicando las validaciones visibles en el cuerpo.
5. `search` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
6. `toggleFavorite` — Invierte el estado booleano asociado al elemento y propaga el cambio a la capa persistente; después actualiza las superficies que dependen de ese estado cuando el cuerpo lo solicita.
7. `togglePin` — Invierte el estado booleano asociado al elemento y propaga el cambio a la capa persistente; después actualiza las superficies que dependen de ese estado cuando el cuerpo lo solicita.

## 9. Qué no debe romperse al modificarlo

- Conservar validaciones de Uri/ruta y no confiar en nombres externos sin sanitizar.

## 10. Resumen en lenguaje sencillo

En términos simples: Fábrica de PendingIntent para abrir app, crear nota, abrir nota/colección, buscar o alternar estados. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.

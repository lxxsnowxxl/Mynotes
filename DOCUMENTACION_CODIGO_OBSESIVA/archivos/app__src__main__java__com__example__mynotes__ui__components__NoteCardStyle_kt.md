# NoteCardStyle.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/components/NoteCardStyle.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `09bcb88572c382a2d3deb45244c8f411f71804a6b43b0780c84f0dbd973303e2`  
**Líneas del código real:** 14

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Funciones/modelos auxiliares para calcular la apariencia de una tarjeta de nota a partir de color, contraste, bordes y configuración.

**Arquitectura.** Mantiene las decisiones visuales fuera de NoteCard para que el composable principal no mezcle todo el cálculo de estilo con el árbol de UI.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.ui.components`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **2 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Jetpack/Compose:** `androidx.compose.runtime.Immutable`.

**Proyecto MyNotes:** `com.example.mynotes.settings.AppSettings`.

## 3. Restricciones e invariantes visibles en el archivo

- No se detectaron automáticamente operadores de restricción comunes; las restricciones específicas siguen documentadas dentro de cada declaración.

## 4. Bloques de código, uno por uno

### 4.1 `AppSettings` — fun, líneas 7–9

```kotlin
data class NoteCardStyle(val cornerRadius: Float, val elevation: Float, val padding: Float, val imageHeight: Float, val titleMaxLines: Int,
    val contentMaxLines: Int, val lineSpacing: Float, val iconSize: Float, val showDate: Boolean, val showCategory: Boolean,
    val showFavorite: Boolean, val animationsEnabled: Boolean, val animationSpeed: Float)
```

#### Qué hace y por qué existe

Funciones/modelos auxiliares para calcular la apariencia de una tarjeta de nota a partir de color, contraste, bordes y configuración.

#### Contrato de la declaración

**Parámetros:**

- `val cornerRadius: Float` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val elevation: Float` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val padding: Float` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val imageHeight: Float` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val titleMaxLines: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val contentMaxLines: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val lineSpacing: Float` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val iconSize: Float` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val showDate: Boolean` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val showCategory: Boolean` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val showFavorite: Boolean` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val animationsEnabled: Boolean` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val animationSpeed: Float` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 8 | `val contentMaxLines` | `Int, val lineSpacing: Float, val iconSize: Float, val showDate: Boolean, val showCategory: Boolean,` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Int, val lineSpacing: Float, val iconSize: Float, val showDate: Boolean, val showCategory: Boolean,`. No declara nulabilidad explícita. |
| 9 | `val showFavorite` | `Boolean, val animationsEnabled: Boolean, val animationSpeed: Float)` | `(sin inicializador en la declaración)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Boolean, val animationsEnabled: Boolean, val animationSpeed: Float)`. No declara nulabilidad explícita. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.2 `AppSettings` — fun, líneas 10–14

```kotlin
fun AppSettings.toNoteCardStyle() = NoteCardStyle(cornerRadius = noteCardCornerRadius, elevation = noteCardElevation,
        padding = noteCardPadding, imageHeight = noteCardImageHeight, titleMaxLines = noteTitleMaxLines,
        contentMaxLines = noteContentMaxLines, lineSpacing = noteLineSpacing, iconSize = iconSize, showDate = showNoteDate,
        showCategory = showCategoryChip, showFavorite = showFavoriteIcon, animationsEnabled = animationsEnabled,
        animationSpeed = animationSpeed)
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `toNoteCardStyle`, `NoteCardStyle`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 8 | `contentMaxLines` | `val` | `Int, val lineSpacing: Float, val iconSize: Float, val showDate: Boolean, val showCategory: Boolean,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Int, val lineSpacing: Float, val iconSize: Float, val showDate: Boolean, val showCategory: Boolean,`. No declara nulabilidad explícita. |
| 9 | `showFavorite` | `val` | `Boolean, val animationsEnabled: Boolean, val animationSpeed: Float)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo declarado es `Boolean, val animationsEnabled: Boolean, val animationSpeed: Float)`. No declara nulabilidad explícita. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

No hay bloques con llaves estructurales en este archivo.

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.

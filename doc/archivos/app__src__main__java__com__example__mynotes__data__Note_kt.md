# Note.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/data/Note.kt`  **SHA-256:** `9214c204b4c1219e4602869c0d979d961f49b5f2d6e76e20afc4aeaeba7c4544`  **Líneas:** 47 · **Bytes:** 1310 · **Imports:** 4 · **Declaraciones detectadas:** 1
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Entidad/modelo persistente de una nota.
## 2. Package e imports

Package declarado: `com.example.mynotes.data`.

### Android / Jetpack / Compose

`androidx.compose.runtime.Immutable`, `androidx.room.Entity`, `androidx.room.Index`, `androidx.room.PrimaryKey`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 24 | `class` | `Note` | `data class Note(` |

## 4. Estado, efectos y límites observables

- **Room:** 3 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.

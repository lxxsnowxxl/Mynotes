# NoteDao.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/data/NoteDao.kt`  **SHA-256:** `7f974ecce790f4e5bcde975e34a669d746ecfe25c96c3e5fab3fe797b94e98db`  **Líneas:** 142 · **Bytes:** 4034 · **Imports:** 7 · **Declaraciones detectadas:** 3
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Consultas Room para notas, filtros y estadísticas.
## 2. Package e imports

Package declarado: `com.example.mynotes.data`.

### Android / Jetpack / Compose

`androidx.room.Dao`, `androidx.room.Delete`, `androidx.room.Insert`, `androidx.room.OnConflictStrategy`, `androidx.room.Query`, `androidx.room.Update`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.flow.Flow`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 10 | `class` | `WidgetNoteStats` | `` |
| 20 | `interface` | `NoteDao` | `` |
| 42 | `fun` | `getAllNotes` | `fun getAllNotes():` |

## 4. Estado, efectos y límites observables

- **Flow/StateFlow:** 2 aparición/apariciones.
- **Room:** 27 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.

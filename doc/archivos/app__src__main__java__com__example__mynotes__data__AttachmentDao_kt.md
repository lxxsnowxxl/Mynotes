# AttachmentDao.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/data/AttachmentDao.kt`  **SHA-256:** `b08c95b65977d9cd903a61de27f1ae385743249f560299c65cae57b7171e1ae1`  **Líneas:** 69 · **Bytes:** 1724 · **Imports:** 6 · **Declaraciones detectadas:** 3
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Consultas Room para adjuntos.
## 2. Package e imports

Package declarado: `com.example.mynotes.data`.

### Android / Jetpack / Compose

`androidx.room.Dao`, `androidx.room.Delete`, `androidx.room.Insert`, `androidx.room.OnConflictStrategy`, `androidx.room.Query`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.flow.Flow`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 9 | `interface` | `AttachmentDao` | `` |
| 23 | `fun` | `getAllAttachments` | `fun getAllAttachments():` |
| 46 | `fun` | `getAttachments` | `fun getAttachments(noteId: Int): Flow<List<Attachment>>` |

## 4. Estado, efectos y límites observables

- **Flow/StateFlow:** 3 aparición/apariciones.
- **Room:** 15 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- No degradar calidad, rutas persistentes ni cachés de adjuntos/miniaturas sin una prueba explícita.

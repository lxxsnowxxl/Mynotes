# Attachment.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/data/Attachment.kt`  **SHA-256:** `d84232af3101044da8e2db5fe8ca0039175d6ff21874589361b982eb0ceb42df`  **Líneas:** 24 · **Bytes:** 570 · **Imports:** 4 · **Declaraciones detectadas:** 1
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Entidad/modelo persistente de adjuntos asociados a una nota.
## 2. Package e imports

Package declarado: `com.example.mynotes.data`.

### Android / Jetpack / Compose

`androidx.compose.runtime.Immutable`, `androidx.room.Entity`, `androidx.room.Index`, `androidx.room.PrimaryKey`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 10 | `class` | `Attachment` | `data class Attachment(` |

## 4. Estado, efectos y límites observables

- **Room:** 2 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- No degradar calidad, rutas persistentes ni cachés de adjuntos/miniaturas sin una prueba explícita.

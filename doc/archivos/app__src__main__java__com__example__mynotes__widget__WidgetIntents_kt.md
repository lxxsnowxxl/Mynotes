# WidgetIntents.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/widget/WidgetIntents.kt`  **SHA-256:** `6699c32c4694794462bbea7ee6c9367655e8c1a09c05cbb0b1b487c0560b58bc`  **Líneas:** 78 · **Bytes:** 3561 · **Imports:** 5 · **Declaraciones detectadas:** 8
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Construcción de PendingIntent/Intent para widgets.
## 2. Package e imports

Package declarado: `com.example.mynotes.widget`.

### Android / Jetpack / Compose

`android.app.PendingIntent`, `android.content.Context`, `android.content.Intent`, `android.net.Uri`

### Proyecto MyNotes

`com.example.mynotes.MainActivity`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 8 | `object` | `WidgetIntents` | `` |
| 12 | `fun` | `openApp` | `` |
| 22 | `fun` | `newNote` | `` |
| 31 | `fun` | `openNote` | `` |
| 41 | `fun` | `openCollection` | `` |
| 51 | `fun` | `search` | `` |
| 60 | `fun` | `toggleFavorite` | `` |
| 69 | `fun` | `togglePin` | `` |

## 4. Estado, efectos y límites observables

- **RemoteViews/widgets:** 17 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.MainActivity`

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.
